/*
*  erros exp: are not processable errors like server failure.

 * // ACTION SALE FLOW
// 1) [POST] \create {params} : merchant service call
//      errors expected: bad request, server

//      a) check valid params @ controller
//      errors expected: NO                              
//
//      b) Invoke SaleServiceImpl create create
//
//          1.b) test idempotence @ repository
//                 errors expected: hibernate          

//          2.b) test valid merchant @ REST
//                i) handle REST request Errors
//                      errors expected: REST errors
//
//          3.b) convert amount @ service
//                 errors expected: NO          

//          4.b) create create entity
//              i) save @ entity
//                  expected errors: Hiberante, Database
//          
//              
// ERROR NOT UNIQUE(merchantId, transactionID) 
// get currency
// ERROR NOT READY 
 */
package aserron.dlocal.demo.pm.data.service;

import antlr.collections.List;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import aserron.dlocal.demo.pm.data.repositories.SaleRepository;
import aserron.dlocal.demo.pm.rest.controllers.MerchantNotFoundException;
import aserron.dlocal.demo.pm.rest.controllers.SaleNotFoundException;
import aserron.dlocal.demo.pm.rest.dto.BalanceResponse;
import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import aserron.dlocal.demo.pm.rest.exception.ErrorMessages;
import java.util.UUID;
import javax.validation.ConstraintViolationException;



@Service
public class SaleServiceImpl implements SaleService {

    private static final Logger logger = Logger.getLogger(SaleServiceImpl.class.getName());

    public static Sale buildSale(CreateSaleRequest saleRequest) {

        // create the new entity.
        Sale sale = new Sale();

        // post params
        sale.setCurrency(saleRequest.getCurrency());
        sale.setAmountOrg(saleRequest.getAmount());
        sale.setAmountUsd(saleRequest.getAmount());
        sale.setMerchantId(saleRequest.getMerchant_id());
        sale.setTransactionId(saleRequest.getTransaction_id());

        // complete values
        sale.setCreated(Date.from(Instant.now()));
        sale.setStatus(TransactionStatus.PENDING);

        return sale;
    }

    // data repo
    private final SaleRepository saleRepository;

    // services
    private final MerchantService merchantService;

    // constructor
    public SaleServiceImpl(SaleRepository saleRepository) {
        this.saleRepository  = saleRepository;
        this.merchantService = new MerchantService();
    }
    
    // accessors
    public SaleRepository getSaleRepository() {
        return saleRepository;
    }

    public MerchantService getMerchantService() {
        return merchantService;
    }

    // main functionality
    
    // POST/sale
    public Sale create(CreateSaleRequest request) {
        
        Sale newSale;
        
        try {

            validateSaleRequest(request);
            
            newSale = createSale(request);            
            
            return persistSale(newSale);

        } catch (MerchantNotFoundException e) {
            
            // consolidate error handling to this service
            throw new SaleServiceException(
                    ErrorMessages.SALE_SERV_INVAL_MERCHANT_ID.getErrorMessage(),
                     e
            );

        }
    }

    // GET/balance
    public BalanceResponse balanceByMerchantId(Long merchantId) {
        BalanceResponse balanceResponse;

        balanceResponse = new BalanceResponse();
        balanceResponse.setMerchantId(merchantId);

        Collection<Sale> sales = this.getSaleRepository().findAllByMerchantId(merchantId);

        logger.info("> balance: MAPPING TRANSACTIONS");

        sales.forEach((Sale t) -> {

            logger.info("status: {}" + t.getStatus());

            balanceResponse.addStatusAmount(
                    t.getStatus(),
                    t.getAmountUsd()
            );

        });

        logger.info("BALANCE: {}" + balanceResponse);

        return balanceResponse;
    }

    // GET/{Id}
    public Sale getById(UUID id) {
        Optional<Sale> sale = this.getSaleRepository().findById(id);

        if (!sale.isPresent()) {
            throw new SaleNotFoundException(id.toString());
        }

        return sale.get();
    }

    // create entity from valid params
    private Sale createSale(CreateSaleRequest params) {
        return buildSale(params);
    }
    
    private Sale persistSale(Sale sale){
        return getSaleRepository().save(sale);
    }

    // Sale request validation
    private void validateSaleRequest(CreateSaleRequest params)
            throws MerchantNotFoundException {
        if (!isValidMerchant(params)) {
            throw new MerchantNotFoundException(
                    params.getMerchant_id().toString());

        } else if (!isValidIdPair(params)) {

            throw new SaleServiceException(
                    ErrorMessages.SALE_SERV_SALE_IDS_NOT_UNIQUE.getErrorMessage());
        } else {

            // all ok, perhaps more validation?
        }
    }

    private boolean isUniqueSale(Sale newSale){
        
        Optional<Sale> oldSale = this.getSaleRepository().findByMerchantIdAndTransactionId(
                        newSale.getMerchantId(),
                        newSale.getTransactionId()
                
                );
        
        if(!oldSale.isPresent()){
            
            return true;
            
        } else if(oldSale.get().equals(newSale)) {
            
            return false;
            
        }else{
        
            return (newSale.getId() != oldSale.get().getId());
        }
        
        
        
        
    }
            
    private boolean isValidIdPair(CreateSaleRequest params) {
    	
        boolean isInvalid;

        // the pair should not be present.
        isInvalid = getSaleRepository()
                .existsByMerchantIdAndTransactionId(
                        params.getMerchant_id(),
                        params.getTransaction_id());

        return (isInvalid == false);
    }

    private boolean isValidMerchant(CreateSaleRequest params) {
        // verify the merchant against the REST service check.
        boolean result = true;
        ResponseEntity<String> checkMerchant;

        checkMerchant = this.getMerchantCheck(params.getMerchant_id());

        if (checkMerchant.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            result = false;
        }

        return result;
    }

    // helper functions
    private ResponseEntity<String> getMerchantCheck(Long merchant_id) {
        ResponseEntity<String> response;
        response = this.merchantService.getMerchantById(merchant_id);
        return response;
    }

}
