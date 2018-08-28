/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.data.service;

/**
 * @see RuntimeException
 * @author Andres
 */
public class SaleServiceException extends RuntimeException {

    private static final long serialVersionUID = -5583045948302072067L;

    public SaleServiceException(String message) {
        super(message);
    }

    public SaleServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaleServiceException(Throwable cause) {
        super(cause);
    }

    public SaleServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
