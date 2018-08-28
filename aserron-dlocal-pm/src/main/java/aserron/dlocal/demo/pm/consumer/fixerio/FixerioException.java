/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.consumer.fixerio;

import java.time.Instant;

class FixerioException extends RuntimeException {
    
    public enum FixerioExceptionReason {
        CURRENCY_NOT_FOUND,
        CONSUMER_NOT_READY,
        CONSUMER_REST_ERROR
    }
    private static final long serialVersionUID = -1723193379509626347L;    
    
    /**
     * Consumer not ready to be used by the service
     * @param targetCurrency
     * @param response
     */
    public FixerioException(
            FixerioExceptionReason reason, Instant lastAccess, String msg) {        
        
        super("FixerIO Service Exception: "
                + reason
                +" :: Last Access=" 
                + lastAccess
                + "  Current Time="
                + Instant.now()
                + " Message= "
                + msg
        );
        
    }
    
}

