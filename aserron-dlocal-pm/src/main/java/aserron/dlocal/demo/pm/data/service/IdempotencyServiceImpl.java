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
    private static final long IN_FLIGHT_WAIT_MS = 2000;
    private static final long IN_FLIGHT_POLL_INTERVAL_MS = 50;

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final org.springframework.transaction.support.TransactionTemplate requiresNewTxTemplate;

    @Autowired
    public IdempotencyServiceImpl(
            IdempotencyRecordRepository idempotencyRecordRepository,
            org.springframework.transaction.PlatformTransactionManager transactionManager
    ) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.requiresNewTxTemplate = new org.springframework.transaction.support.TransactionTemplate(transactionManager);
        this.requiresNewTxTemplate.setPropagationBehavior(org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
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
        Optional<IdempotencyRecord> existingOpt = requiresNewTxTemplate.execute(status ->
                idempotencyRecordRepository.findByClientIdAndOperationNameAndIdempotencyKey(clientId, operationName, idempotencyKey)
        );

        if (existingOpt != null && existingOpt.isPresent()) {
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
            IdempotencyRecord saved = requiresNewTxTemplate.execute(status ->
                    idempotencyRecordRepository.saveAndFlush(newRecord)
            );
            logger.debug("Claimed new idempotency lease {} for key [{}]", saved.getId(), idempotencyKey);
            return ClaimResult.newClaim(saved);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Concurrent collision detected on idempotency claim for key [{}]", idempotencyKey);
            Optional<IdempotencyRecord> collidedOpt = requiresNewTxTemplate.execute(status ->
                    idempotencyRecordRepository.findByClientIdAndOperationNameAndIdempotencyKey(clientId, operationName, idempotencyKey)
            );
            IdempotencyRecord collided = (collidedOpt != null && collidedOpt.isPresent()) ? collidedOpt.get() : null;
            if (collided == null) {
                throw e;
            }

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
                Optional<IdempotencyRecord> pollOpt = requiresNewTxTemplate.execute(status ->
                        idempotencyRecordRepository.findByClientIdAndOperationNameAndIdempotencyKey(clientId, operationName, idempotencyKey)
                );
                if (pollOpt != null && pollOpt.isPresent() && pollOpt.get().getStatus() == IdempotencyStatus.COMPLETED) {
                    logger.info("Idempotency in-flight race resolved: returning completed result for key [{}]", idempotencyKey);
                    return ClaimResult.completed(pollOpt.get());
                }
            }

            logger.info("Idempotency collision: matching request for key [{}] remains in-flight", existing.getIdempotencyKey());
            return ClaimResult.inFlight(existing);
        }

        if (existing.getStatus() == IdempotencyStatus.FAILED) {
            logger.info("Idempotency retry: previous attempt failed for key [{}]. Granting new claim lease.", existing.getIdempotencyKey());
            IdempotencyRecord updated = requiresNewTxTemplate.execute(status -> {
                Optional<IdempotencyRecord> recOpt = idempotencyRecordRepository.findById(existing.getId());
                if (recOpt.isPresent()) {
                    IdempotencyRecord rec = recOpt.get();
                    rec.setStatus(IdempotencyStatus.PENDING);
                    rec.setRequestHash(requestHash);
                    Instant now = Instant.now();
                    rec.setCreatedAt(Date.from(now));
                    rec.setExpiresAt(Date.from(now.plus(ttl != null ? ttl : Duration.ofHours(24))));
                    return idempotencyRecordRepository.saveAndFlush(rec);
                }
                return existing;
            });
            return ClaimResult.retriableFailed(updated);
        }

        return ClaimResult.inFlight(existing);
    }

    @Override
    public void complete(UUID recordId, int httpStatus, String responseBody) {
        if (recordId == null) {
            return;
        }
        requiresNewTxTemplate.execute(status -> {
            idempotencyRecordRepository.findById(recordId).ifPresent(record -> {
                record.markCompleted(httpStatus, responseBody);
                idempotencyRecordRepository.saveAndFlush(record);
                logger.debug("Marked idempotency record {} as COMPLETED (HTTP {})", recordId, httpStatus);
            });
            return null;
        });
    }

    @Override
    public void fail(UUID recordId) {
        if (recordId == null) {
            return;
        }
        requiresNewTxTemplate.execute(status -> {
            idempotencyRecordRepository.findById(recordId).ifPresent(record -> {
                record.markFailed();
                idempotencyRecordRepository.saveAndFlush(record);
                logger.warn("Marked idempotency record {} as FAILED", recordId);
            });
            return null;
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
