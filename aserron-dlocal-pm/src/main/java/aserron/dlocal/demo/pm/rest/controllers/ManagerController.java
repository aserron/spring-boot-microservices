/*
 
    Not Implemented signatures

        @PutMapping("/{id}")
        public ResponseEntity<?> put(@PathVariable String id, @RequestBody Object input) 

        @PostMapping
        public ResponseEntity<?> post(@RequestBody Object input)

        @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(@PathVariable String id) 

 */
package aserron.dlocal.demo.pm.rest.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.service.MerchantService;
import aserron.dlocal.demo.pm.data.service.SaleService;
import aserron.dlocal.demo.pm.data.service.TransactionJobService;
import aserron.dlocal.demo.pm.rest.dto.BalanceResponse;
import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import aserron.dlocal.demo.pm.rest.dto.StatusResponse;
import aserron.dlocal.demo.pm.rest.exception.ApiError;
import java.util.UUID;

/**
 * Control REST routes, and method implementation. Handle invocation exceptions.
 * Runs a timed scheduled task.
 *
 * @author Andres
 */
@RestController
@Validated
@EnableScheduling
@RequestMapping("/pm")
public class ManagerController {

    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    public  static Logger getLogger() {
        return logger;
    }
    
    // instance members

    // services
    private final SaleService saleService;
    @SuppressWarnings("unused")
    private final TransactionJobService jobService;
    private final MerchantService merchantService;

    /**
     * Constructor
     * 
     * @param saleService A SaleService instance. (injected)
     */
    @Autowired
    public ManagerController(SaleService saleService) {

        this.saleService     = saleService;
        this.jobService      = new TransactionJobService(saleService.getSaleRepository());
        this.merchantService = new MerchantService();

    }

    /**
     * POST /sale 
     * @param params
     * @return 
     */
    @PostMapping("/sale")
    public Sale createSale(
            @Valid @RequestBody CreateSaleRequest params) 
    {
        return getSaleService().create(params);
    }

    /**
     * GET /status/{id} 
     * @param id
     * @return 
     */
    @GetMapping("/status/{id}")
    public StatusResponse getMerchantStatus(
                @Valid
                @PathVariable(name = "id",required = true)
                @Pattern(regexp = "^[0-9]{1,+}$", message = "Integer number expected for [id]")                
                String id )
    {
        return StatusResponse.createFrom(getSaleService().getById(UUID.fromString(id)));
    }
    
    /**
     * POST /balance 
     * @param id Merchant ID
     * 
     * @return Balance for the given Merchant
     */
    @GetMapping("/balance/{id}")
    public BalanceResponse balance(@PathVariable String id) {
        return getSaleService().balanceByMerchantId(Long.parseLong(id));
    }
    
    
    /**
     * GET /all/status 
     * List status for all sale in the system
     * Debug action.
     * @return 
     */
    @GetMapping("/all/status")
    public Collection<Sale> list() {
        return this.getSaleService().getSaleRepository().findAll();

    }


    // Error Handling    
    @ExceptionHandler(SaleNotFoundException.class)
    public ResponseEntity<Object> handleError(HttpServletRequest req, RuntimeException ex) {
        // return new ResponseEntity<>(new Object(),HttpStatus.NOT_FOUND);
        List<String> errors = new ArrayList<String>();
        errors.add(req.getQueryString());
        
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, 
                                            ex.getLocalizedMessage(), 
                                            errors
                                        );
        

         return new ResponseEntity<Object>(
                        apiError, 
                        new HttpHeaders(), 
                        apiError.getStatus()
                    );
        
    }
    
    // Setters & Getters      
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
