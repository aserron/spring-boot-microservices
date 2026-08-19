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
import aserron.dlocal.demo.pm.rest.exception.IdempotencyFingerprintMismatchException;
import aserron.dlocal.demo.pm.rest.exception.IdempotencyRequestInFlightException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SaleServiceImpl implements SaleService {

    private static final Logger logger = LoggerFactory.getLogger(SaleServiceImpl.class);

    private final SaleRepository saleRepository;
    private final MerchantService merchantService;
    private final FixerioService fixerioService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SaleServiceImpl(
            SaleRepository saleRepository,
            MerchantService merchantService,
            FixerioService fixerioService,
            IdempotencyService idempotencyService,
            ObjectMapper objectMapper
    ) {
        this.saleRepository = saleRepository;
        this.merchantService = merchantService;
        this.fixerioService = fixerioService;
        this.idempotencyService = idempotencyService;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    @Override
    public Sale create(CreateSaleRequest request) {
        return create(request, null);
    }

    @Override
    public Sale create(CreateSaleRequest request, String idempotencyKey) {
        if (request == null) {
            throw new SaleServiceException("Sale request cannot be null");
        }

        // 1. Determine Effective Idempotency Key & Compute SHA-256 Hash
        String effectiveKey = (idempotencyKey != null && !idempotencyKey.trim().isEmpty())
                ? idempotencyKey.trim()
                : "merchant_" + request.getMerchant_id() + ":tx_" + request.getTransaction_id();

        String clientId = request.getMerchant_id() != null ? request.getMerchant_id().toString() : "unknown";
        String operationName = "POST /pm/sale";
        String requestHash = idempotencyService.computeHash(request);

        // 2. Claim Lease from Idempotency Engine
        ClaimResult claim = idempotencyService.claim(clientId, operationName, effectiveKey, requestHash, Duration.ofHours(24));

        if (claim.isCompleted()) {
            logger.info("Idempotency replay hit for key [{}]: returning cached sale", effectiveKey);
            return resolveCompletedSale(claim, request);
        }

        if (claim.isInFlight()) {
            logger.warn("Idempotency in-flight collision for key [{}]", effectiveKey);
            throw new IdempotencyRequestInFlightException("A matching request with idempotency key [" + effectiveKey + "] is currently in progress.");
        }

        if (claim.isMismatch()) {
            logger.warn("Idempotency payload mismatch for key [{}]", effectiveKey);
            throw new IdempotencyFingerprintMismatchException("Idempotency key [" + effectiveKey + "] was reused with differing payload parameters.");
        }

        // 3. Execute Business Mutation
        try {
            // Validate Merchant via REST call
            validateMerchant(request.getMerchant_id());

            // Convert Amount to USD using Fixer.io
            BigDecimal amountUsd = fixerioService.convertCurrencyAmount(request.getCurrency(), request.getAmount());

            // Build Sale entity
            Sale sale = new Sale();
            sale.setMerchantId(request.getMerchant_id());
            sale.setTransactionId(request.getTransaction_id());
            sale.setCurrency(request.getCurrency().toUpperCase());
            sale.setAmountOrg(request.getAmount());
            sale.setAmountUsd(amountUsd);
            sale.setStatus(TransactionStatus.PENDING);
            sale.setCreated(Date.from(Instant.now()));

            // Domain Invariant Flush (with collision recovery fallback)
            Sale savedSale;
            try {
                savedSale = saleRepository.saveAndFlush(sale);
            } catch (DataIntegrityViolationException e) {
                logger.warn("Domain unique constraint collision detected for merchant {} and transaction {}. Recovering record.",
                        request.getMerchant_id(), request.getTransaction_id());
                savedSale = saleRepository.findByMerchantIdAndTransactionId(request.getMerchant_id(), request.getTransaction_id())
                        .orElseThrow(() -> e);
            }

            // Mark Idempotency Record COMPLETED with Response JSON
            String responseJson = "{\"id\":\"" + savedSale.getId().toString() + "\"}";
            idempotencyService.complete(claim.getRecordId(), 200, responseJson);

            return savedSale;
        } catch (Exception ex) {
            idempotencyService.fail(claim.getRecordId());
            throw ex;
        }
    }

    private Sale resolveCompletedSale(ClaimResult claim, CreateSaleRequest request) {
        if (claim.getResponseBody() != null) {
            try {
                JsonNode node = objectMapper.readTree(claim.getResponseBody());
                if (node.has("id")) {
                    UUID saleId = UUID.fromString(node.get("id").asText());
                    Optional<Sale> cached = saleRepository.findById(saleId);
                    if (cached.isPresent()) {
                        return cached.get();
                    }
                }
            } catch (Exception e) {
                logger.debug("Failed to deserialize cached response body: {}", e.getMessage());
            }
        }
        return saleRepository.findByMerchantIdAndTransactionId(request.getMerchant_id(), request.getTransaction_id())
                .orElseThrow(() -> new IllegalStateException("Sale entity should exist for completed idempotency key"));
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

    public IdempotencyService getIdempotencyService() {
        return idempotencyService;
    }
}
