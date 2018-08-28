package aserron.dlocal.merchant.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class MerchantNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6633618362841877195L;

    
    public MerchantNotFoundException(Long merchantId) {
        super("Merchant Not Found: with id=" + merchantId);
    }
}
