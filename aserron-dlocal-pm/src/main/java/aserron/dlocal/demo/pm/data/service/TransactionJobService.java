package aserron.dlocal.demo.pm.data.service;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import aserron.dlocal.demo.pm.data.repositories.SaleRepository;
import java.time.LocalTime;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionJobService {

    private static final int JOB_TIME = 30000;
    private static final Logger log = LoggerFactory.getLogger(TransactionJobService.class);

    private final SaleRepository saleRepository;

    @Autowired
    public TransactionJobService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
        log.info("TransactionJobService initialized at {}", LocalTime.now());
    }

    @Scheduled(fixedRate = JOB_TIME)
    @Transactional
    public void scheduledTask() {
        log.info("[{}] JOB: Executing 30s batch transaction processing", LocalTime.now());
        processTransactions();
    }

    @Transactional
    public void processTransactions() {
        Collection<Sale> pendingSales = saleRepository.findAllByStatus(TransactionStatus.PENDING);
        if (pendingSales == null || pendingSales.isEmpty()) {
            return;
        }

        for (Sale sale : pendingSales) {
            if (isPaid()) {
                sale.setStatus(TransactionStatus.PAID);
            } else {
                sale.setStatus(TransactionStatus.REJECTED);
            }
            saleRepository.save(sale);
        }
        log.info("Batch processed {} PENDING transactions", pendingSales.size());
    }

    /**
     * Requirement: 70% probability to transition PENDING transaction to PAID,
     * remaining (30%) to REJECTED.
     */
    private boolean isPaid() {
        return Math.random() < 0.70;
    }
}
