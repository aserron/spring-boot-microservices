package aserron.dlocal.demo.pm.data.repositories;

import aserron.dlocal.demo.pm.data.domain.IdempotencyRecord;
import aserron.dlocal.demo.pm.data.domain.IdempotencyStatus;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, UUID> {

    Optional<IdempotencyRecord> findByClientIdAndOperationNameAndIdempotencyKey(
            String clientId,
            String operationName,
            String idempotencyKey
    );

    @Modifying
    @Transactional
    long deleteByStatusAndExpiresAtBefore(IdempotencyStatus status, Date expiresAt);
}
