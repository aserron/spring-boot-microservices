package aserron.dlocal.demo.pm.data.service;

import aserron.dlocal.demo.pm.data.domain.IdempotencyRecord;
import aserron.dlocal.demo.pm.data.domain.IdempotencyStatus;
import aserron.dlocal.demo.pm.data.repositories.IdempotencyRecordRepository;
import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdempotencyServiceImpl implements IdempotencyService {

    private static final Logger logger = LoggerFactory.getLogger(IdempotencyServiceImpl.class);
    private static final long IN_FLIGHT_WAIT_MS = 500;
    private static final long IN_FLIGHT_POLL_INTERVAL_MS = 50;

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    @Autowired
    public IdempotencyServiceImpl(IdempotencyRecordRepository idempotencyRecordRepository) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
    }

    @Override
    @Transactional
    public ClaimResult claim(
            String clientId,
            String operationName,
            String idempotencyKey,
            String requestHash,
            Duration ttl
    ) {
        if (clientId == null || operationName == null || idempotencyKey == null || requestHash == null) {
            throw new IllegalArgumentException("Idempotency parameters (clientId, operationName, idempotencyKey, requestHash) cannot be null");
        }

        // 1. Fast-Path Pre-Check
        Optional<IdempotencyRecord> existingOpt = idempotencyRecordRepository
                .findByClientIdAndOperationNameAndIdempotencyKey(clientId, operationName, idempotencyKey);

        if (existingOpt.isPresent()) {
            return evaluateExistingRecord(existingOpt.get(), clientId, operationName, idempotencyKey, requestHash, ttl);
        }

        // 2. Optimistic Claim (INSERT as PENDING)
        IdempotencyRecord newRecord = IdempotencyRecord.createPending(
                clientId,
                operationName,
                idempotencyKey,
                requestHash,
                ttl
        );

        try {
            IdempotencyRecord saved = idempotencyRecordRepository.saveAndFlush(newRecord);
            logger.debug("Claimed new idempotency lease {} for key [{}]", saved.getId(), idempotencyKey);
            return ClaimResult.newClaim(saved);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Concurrent collision detected on idempotency claim for key [{}]", idempotencyKey);
            IdempotencyRecord collided = idempotencyRecordRepository
                    .findByClientIdAndOperationNameAndIdempotencyKey(clientId, operationName, idempotencyKey)
                    .orElseThrow(() -> e);

            return evaluateExistingRecord(collided, clientId, operationName, idempotencyKey, requestHash, ttl);
        }
    }

    private ClaimResult evaluateExistingRecord(
            IdempotencyRecord existing,
            String clientId,
            String operationName,
            String idempotencyKey,
            String requestHash,
            Duration ttl
    ) {
        if (existing.getStatus() == IdempotencyStatus.COMPLETED) {
            if (!existing.getRequestHash().equals(requestHash)) {
                logger.warn("Idempotency key [{}] reused with differing request payload hash", existing.getIdempotencyKey());
                return ClaimResult.mismatch(existing);
            }
            logger.info("Idempotency hit: replaying completed response for key [{}]", existing.getIdempotencyKey());
            return ClaimResult.completed(existing);
        }

        if (existing.getStatus() == IdempotencyStatus.PENDING) {
            if (!existing.getRequestHash().equals(requestHash)) {
                logger.warn("Idempotency key [{}] reused with differing payload while still PENDING", existing.getIdempotencyKey());
                return ClaimResult.mismatch(existing);
            }

            // Micro-race resolution: Poll briefly to allow active concurrent execution to complete
            long deadline = System.currentTimeMillis() + IN_FLIGHT_WAIT_MS;
            while (System.currentTimeMillis() < deadline) {
                try {
                    Thread.sleep(IN_FLIGHT_POLL_INTERVAL_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                Optional<IdempotencyRecord> pollOpt = idempotencyRecordRepository
                        .findByClientIdAndOperationNameAndIdempotencyKey(clientId, operationName, idempotencyKey);
                if (pollOpt.isPresent() && pollOpt.get().getStatus() == IdempotencyStatus.COMPLETED) {
                    logger.info("Idempotency in-flight race resolved: returning completed result for key [{}]", idempotencyKey);
                    return ClaimResult.completed(pollOpt.get());
                }
            }

            logger.info("Idempotency collision: matching request for key [{}] remains in-flight", existing.getIdempotencyKey());
            return ClaimResult.inFlight(existing);
        }

        if (existing.getStatus() == IdempotencyStatus.FAILED) {
            logger.info("Idempotency retry: previous attempt failed for key [{}]. Granting new claim lease.", existing.getIdempotencyKey());
            existing.setStatus(IdempotencyStatus.PENDING);
            existing.setRequestHash(requestHash);
            Instant now = Instant.now();
            existing.setCreatedAt(Date.from(now));
            existing.setExpiresAt(Date.from(now.plus(ttl != null ? ttl : Duration.ofHours(24))));
            IdempotencyRecord updated = idempotencyRecordRepository.saveAndFlush(existing);
            return ClaimResult.retriableFailed(updated);
        }

        return ClaimResult.inFlight(existing);
    }

    @Override
    @Transactional
    public void complete(UUID recordId, int httpStatus, String responseBody) {
        if (recordId == null) {
            return;
        }
        idempotencyRecordRepository.findById(recordId).ifPresent(record -> {
            record.markCompleted(httpStatus, responseBody);
            idempotencyRecordRepository.save(record);
            logger.debug("Marked idempotency record {} as COMPLETED (HTTP {})", recordId, httpStatus);
        });
    }

    @Override
    @Transactional
    public void fail(UUID recordId) {
        if (recordId == null) {
            return;
        }
        idempotencyRecordRepository.findById(recordId).ifPresent(record -> {
            record.markFailed();
            idempotencyRecordRepository.save(record);
            logger.warn("Marked idempotency record {} as FAILED", recordId);
        });
    }

    @Override
    public String computeHash(CreateSaleRequest request) {
        if (request == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = String.format("%s:%s:%s:%s",
                    request.getMerchant_id() != null ? request.getMerchant_id() : "",
                    request.getTransaction_id() != null ? request.getTransaction_id() : "",
                    request.getCurrency() != null ? request.getCurrency().trim().toUpperCase() : "",
                    request.getAmount() != null ? request.getAmount().stripTrailingZeros().toPlainString() : ""
            );
            byte[] encoded = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder("sha256:");
            for (byte b : encoded) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 digest algorithm not available", e);
        }
    }
}
