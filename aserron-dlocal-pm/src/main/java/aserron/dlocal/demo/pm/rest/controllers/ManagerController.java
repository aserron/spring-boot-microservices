package aserron.dlocal.demo.pm.rest.controllers;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.service.MerchantService;
import aserron.dlocal.demo.pm.data.service.SaleService;
import aserron.dlocal.demo.pm.data.service.TransactionJobService;
import aserron.dlocal.demo.pm.rest.dto.BalanceResponse;
import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import aserron.dlocal.demo.pm.rest.dto.StatusResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@EnableScheduling
@RequestMapping("/pm")
public class ManagerController {

    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    private final SaleService saleService;
    private final TransactionJobService jobService;
    private final MerchantService merchantService;

    @Autowired
    public ManagerController(SaleService saleService, TransactionJobService jobService, MerchantService merchantService) {
        this.saleService = saleService;
        this.jobService = jobService;
        this.merchantService = merchantService;
    }

    /**
     * POST /pm/sale
     * Response: { "id": String }
     */
    @PostMapping("/sale")
    public ResponseEntity<Map<String, String>> createSale(
            @Valid @RequestBody CreateSaleRequest params,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        Sale sale = saleService.create(params, idempotencyKey);
        return ResponseEntity.ok(Collections.singletonMap("id", sale.getId().toString()));
    }

    /**
     * GET /pm/status/{id}
     */
    @GetMapping("/status/{id}")
    public ResponseEntity<StatusResponse> getMerchantStatus(@PathVariable("id") String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for sale ID: " + id);
        }
        Sale sale = saleService.getById(uuid);
        return ResponseEntity.ok(StatusResponse.createFrom(sale));
    }

    /**
     * GET /pm/balance?merchant_id=...&from=...&to=...
     */
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(
            @RequestParam(name = "merchant_id", required = true) Long merchantId,
            @RequestParam(name = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(name = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        Date fromDate = from != null ? Date.from(from.atZone(ZoneId.systemDefault()).toInstant()) : null;
        Date toDate = to != null ? Date.from(to.atZone(ZoneId.systemDefault()).toInstant()) : null;

        BalanceResponse balance = saleService.balance(merchantId, fromDate, toDate);
        return ResponseEntity.ok(balance);
    }

    /**
     * GET /pm/balance/{id}
     */
    @GetMapping("/balance/{id}")
    public ResponseEntity<BalanceResponse> getBalanceByPath(@PathVariable("id") Long id) {
        return ResponseEntity.ok(saleService.balanceByMerchantId(id));
    }

    /**
     * GET /pm/all/status
     */
    @GetMapping("/all/status")
    public ResponseEntity<Collection<Sale>> listAllSales() {
        return ResponseEntity.ok(saleService.getSaleRepository().findAll());
    }

    public SaleService getSaleService() {
        return saleService;
    }

    public TransactionJobService getJobService() {
        return jobService;
    }

    public MerchantService getMerchantService() {
        return merchantService;
    }
}
