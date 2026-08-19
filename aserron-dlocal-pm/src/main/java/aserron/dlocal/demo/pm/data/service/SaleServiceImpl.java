package aserron.dlocal.demo.pm.data.service;

import aserron.dlocal.demo.pm.consumer.fixerio.FixerioService;
import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import aserron.dlocal.demo.pm.data.repositories.SaleRepository;
import aserron.dlocal.demo.pm.rest.controllers.MerchantNotFoundException;
import aserron.dlocal.demo.pm.rest.controllers.SaleNotFoundException;
import aserron.dlocal.demo.pm.rest.dto.BalanceResponse;
import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import aserron.dlocal.demo.pm.rest.exception.ErrorMessages;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SaleServiceImpl implements SaleService {

    private static final Logger logger = LoggerFactory.getLogger(SaleServiceImpl.class);

    private final SaleRepository saleRepository;
    private final MerchantService merchantService;
    private final FixerioService fixerioService;

    @Autowired
    public SaleServiceImpl(SaleRepository saleRepository, MerchantService merchantService, FixerioService fixerioService) {
        this.saleRepository = saleRepository;
        this.merchantService = merchantService;
        this.fixerioService = fixerioService;
    }

    @Override
    public Sale create(CreateSaleRequest request) {
        if (request == null) {
            throw new SaleServiceException("Sale request cannot be null");
        }

        // 1. Validate Merchant via REST call to Merchant app
        validateMerchant(request.getMerchant_id());

        // 2. Idempotency Check: if tuple (merchant_id, transaction_id) exists, return the existing sale
        Optional<Sale> existing = saleRepository.findByMerchantIdAndTransactionId(
                request.getMerchant_id(),
                request.getTransaction_id()
        );
        if (existing.isPresent()) {
            logger.info("Idempotency hit for merchant {} and transaction {}: returning existing sale {}",
                    request.getMerchant_id(), request.getTransaction_id(), existing.get().getId());
            return existing.get();
        }

        // 3. Convert Amount to USD using Fixer.io
        BigDecimal amountUsd = fixerioService.convertCurrencyAmount(request.getCurrency(), request.getAmount());

        // 4. Build and persist new Sale
        Sale sale = new Sale();
        sale.setMerchantId(request.getMerchant_id());
        sale.setTransactionId(request.getTransaction_id());
        sale.setCurrency(request.getCurrency().toUpperCase());
        sale.setAmountOrg(request.getAmount());
        sale.setAmountUsd(amountUsd);
        sale.setStatus(TransactionStatus.PENDING);
        sale.setCreated(Date.from(Instant.now()));

        return saleRepository.save(sale);
    }

    @Override
    public Sale getById(UUID id) {
        if (id == null) {
            throw new SaleNotFoundException("null");
        }
        return saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException(id.toString()));
    }

    @Override
    public BalanceResponse balanceByMerchantId(Long merchantId) {
        return balance(merchantId, null, null);
    }

    @Override
    public BalanceResponse balance(Long merchantId, Date from, Date to) {
        if (merchantId == null) {
            throw new SaleServiceException("Merchant ID cannot be null");
        }

        Collection<Sale> sales;
        if (from != null && to != null) {
            sales = saleRepository.findAllByMerchantIdAndCreatedBetween(merchantId, from, to);
        } else if (from != null) {
            sales = saleRepository.findAllByMerchantIdAndCreatedGreaterThanEqual(merchantId, from);
        } else if (to != null) {
            sales = saleRepository.findAllByMerchantIdAndCreatedLessThanEqual(merchantId, to);
        } else {
            sales = saleRepository.findAllByMerchantId(merchantId);
        }

        BalanceResponse response = new BalanceResponse();
        response.setMerchantId(merchantId);

        if (sales != null) {
            for (Sale sale : sales) {
                if (sale.getStatus() != null && sale.getAmountUsd() != null) {
                    response.addStatusAmount(sale.getStatus(), sale.getAmountUsd());
                }
            }
        }

        return response;
    }

    private void validateMerchant(Long merchantId) {
        if (merchantId == null) {
            throw new SaleServiceException(ErrorMessages.SALE_SERV_INVAL_MERCHANT_ID.getErrorMessage());
        }

        try {
            ResponseEntity<String> response = merchantService.getMerchantById(merchantId);
            if (response == null || response.getStatusCode() != HttpStatus.OK) {
                throw new MerchantNotFoundException(merchantId.toString());
            }
        } catch (MerchantNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("Validation failed for merchant {}: {}", merchantId, e.getMessage());
            throw new MerchantNotFoundException(merchantId.toString());
        }
    }

    @Override
    public SaleRepository getSaleRepository() {
        return saleRepository;
    }

    @Override
    public MerchantService getMerchantService() {
        return merchantService;
    }

    public FixerioService getFixerioService() {
        return fixerioService;
    }
}
