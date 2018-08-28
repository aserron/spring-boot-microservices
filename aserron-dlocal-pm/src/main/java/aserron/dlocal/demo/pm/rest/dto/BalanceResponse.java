package aserron.dlocal.demo.pm.rest.dto;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import java.io.Serializable;
import java.math.BigDecimal;
/**
 * Model the "balance" REST action response.
 * @author Andres
 */

public class BalanceResponse implements Serializable {

    private static final long serialVersionUID = 488895936461508228L;
    
    private long merchantId;

    private BigDecimal total_paid     = BigDecimal.ZERO;
    private BigDecimal total_pending  = BigDecimal.ZERO;
    private BigDecimal total_rejected = BigDecimal.ZERO;
    
    /**
     * Adds an amount to a given status total money amount.
     * Helper function for the balance REST action.
     * @param status
     * @param amount 
     */
    public void addStatusAmount (TransactionStatus status,BigDecimal amount){
        
        switch (status) {
            case PAID:
                this.setTotal_paid(getTotal_paid().add(amount));
                break;
            case PENDING:
                this.setTotal_pending(getTotal_pending().add(amount));
                break;
            case REJECTED:
                this.setTotal_rejected(getTotal_rejected().add(amount));
                break;
            default:
                break;
        }
        
    }

    @Override
    public String toString() {
        return "Merchant id:"
                + this.merchantId
                +" balance > "
                +" pending=" + this.getTotal_pending()
                +" paid=" +this.getTotal_paid()
                +" rejected="+this.getTotal_rejected()
                +"";
                
    }
    
    
    // setters & getters
    
    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getTotal_paid() {
        return total_paid;
    }

    public void setTotal_paid(BigDecimal total_paid) {
        this.total_paid = total_paid;
    }

    public BigDecimal getTotal_pending() {
        return total_pending;
    }

    public void setTotal_pending(BigDecimal total_pending) {
        this.total_pending = total_pending;
    }

    public BigDecimal getTotal_rejected() {
        return total_rejected;
    }

    public void setTotal_rejected(BigDecimal total_rejected) {
        this.total_rejected = total_rejected;
    }

    
    
    
    
       
    
}
