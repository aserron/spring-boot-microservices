/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.NOT_FOUND, 
        reason = "This customer is not found in the system"
)
public class MerchantNotFoundException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1403853627207818560L;

	public MerchantNotFoundException(String message) {
        super(message);
    }
        
}