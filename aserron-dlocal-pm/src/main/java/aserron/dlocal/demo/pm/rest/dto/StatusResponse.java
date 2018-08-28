/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.rest.dto;

import java.text.SimpleDateFormat;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;

public class StatusResponse {
	
	private static String DATE_FORMAT = "yyyyy-mm-dd hh:mm:ss";
    
    public static StatusResponse createFrom(Sale sale){
        
        SimpleDateFormat dt = new SimpleDateFormat(DATE_FORMAT);
        
        StatusResponse response = new StatusResponse();
        
        response.setId(sale.getId().toString());        
        response.setMerchant_id(sale.getMerchantId().toString());
        response.setTransaction_id(sale.getTransactionId().toString());
        
        response.setDate(dt.format(sale.getCreated()));
        response.setStatus(sale.getStatus());
        
        return response;
    };

    private String id;
    private String date;
    private String merchant_id;
    private String transaction_id;
    private TransactionStatus status;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the merchant_id
     */
    public String getMerchant_id() {
        return merchant_id;
    }

    /**
     * @param merchant_id the merchant_id to set
     */
    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

  

    /**
     * @return the status
     */
    public TransactionStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

}
