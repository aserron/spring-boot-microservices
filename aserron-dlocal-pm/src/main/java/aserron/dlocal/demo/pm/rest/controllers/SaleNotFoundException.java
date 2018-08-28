package aserron.dlocal.demo.pm.rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value  = HttpStatus.NOT_FOUND,
        reason = SaleNotFoundException.REASON
)
public class SaleNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -4869709808328666224L;

    public static final String REASON ="The <sale> for the ID passed is not found in the system: id=";

    public SaleNotFoundException(String id) {        
        super(REASON + id);
    }
}