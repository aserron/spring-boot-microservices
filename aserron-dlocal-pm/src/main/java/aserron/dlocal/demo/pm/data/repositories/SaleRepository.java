package aserron.dlocal.demo.pm.data.repositories;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface SaleRepository extends CrudRepository<Sale, UUID> {

    @Override
    Collection<Sale> findAll();

    Collection<Sale> findAllByMerchantId(Long merchantId);

    Collection<Sale> findAllByStatus(TransactionStatus status);

    Collection<Sale> findAllByMerchantIdAndCreatedBetween(Long merchantId, Date from, Date to);

    Collection<Sale> findAllByMerchantIdAndCreatedGreaterThanEqual(Long merchantId, Date from);

    Collection<Sale> findAllByMerchantIdAndCreatedLessThanEqual(Long merchantId, Date to);

    Optional<Sale> findByMerchantIdAndTransactionId(Long merchantId, Long transactionId);

    boolean existsByMerchantIdAndTransactionId(Long merchantId, Long transactionId);
}
