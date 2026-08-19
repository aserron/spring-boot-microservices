package aserron.dlocal.demo.pm.data.repositories;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

    @Override
    List<Sale> findAll();

    Collection<Sale> findAllByMerchantId(Long merchantId);

    Collection<Sale> findAllByStatus(TransactionStatus status);

    Collection<Sale> findAllByMerchantIdAndCreatedBetween(Long merchantId, Date from, Date to);

    Collection<Sale> findAllByMerchantIdAndCreatedGreaterThanEqual(Long merchantId, Date from);

    Collection<Sale> findAllByMerchantIdAndCreatedLessThanEqual(Long merchantId, Date to);

    Optional<Sale> findByMerchantIdAndTransactionId(Long merchantId, Long transactionId);

    boolean existsByMerchantIdAndTransactionId(Long merchantId, Long transactionId);
}
