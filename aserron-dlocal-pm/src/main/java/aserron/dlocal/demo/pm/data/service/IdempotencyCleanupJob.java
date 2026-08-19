package aserron.dlocal.demo.pm.data.service;

import aserron.dlocal.demo.pm.data.domain.IdempotencyStatus;
import aserron.dlocal.demo.pm.data.repositories.IdempotencyRecordRepository;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdempotencyCleanupJob {

    private static final Logger logger = LoggerFactory.getLogger(IdempotencyCleanupJob.class);

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    @Autowired
    public IdempotencyCleanupJob(IdempotencyRecordRepository idempotencyRecordRepository) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
    }

    /**
     * Periodically purges expired COMPLETED idempotency records.
     * PENDING records are explicitly retained to prevent race conditions during long-running execution.
     */
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void purgeExpiredRecords() {
        Date now = Date.from(Instant.now());
        long deletedCount = idempotencyRecordRepository.deleteByStatusAndExpiresAtBefore(IdempotencyStatus.COMPLETED, now);
        if (deletedCount > 0) {
            logger.info("Purged {} expired COMPLETED idempotency records", deletedCount);
        }
    }
}
