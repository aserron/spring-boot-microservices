/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateSaleRequest implements Serializable {

    private static final long serialVersionUID = -3538551675304923702L;
    
    
    @Valid
    @NotNull
    private String currency;
    
    @Valid
    @Min(value = 0)
    private BigDecimal amount;
    
    @Valid
    @NotNull
    @Min(value = 1)
    private Long transaction_id;
    
    @Valid
    @NotNull
    @Min(value = 1)
    private Long merchant_id;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(Long transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Long getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(Long merchant_id) {
        this.merchant_id = merchant_id;
    }
    
    
    
    
}
