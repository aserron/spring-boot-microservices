package aserron.dlocal.demo.pm.rest.dto;

import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.math.BigDecimal;

public class BalanceResponse implements Serializable {

    private static final long serialVersionUID = 488895936461508228L;

    @JsonIgnore
    private long merchantId;

    private BigDecimal total_paid = BigDecimal.ZERO;
    private BigDecimal total_pending = BigDecimal.ZERO;
    private BigDecimal total_rejected = BigDecimal.ZERO;

    public void addStatusAmount(TransactionStatus status, BigDecimal amount) {
        if (status == null || amount == null) {
            return;
        }
        switch (status) {
            case PAID:
                this.setTotal_paid(this.getTotal_paid().add(amount));
                break;
            case PENDING:
                this.setTotal_pending(this.getTotal_pending().add(amount));
                break;
            case REJECTED:
                this.setTotal_rejected(this.getTotal_rejected().add(amount));
                break;
            default:
                break;
        }
    }

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

    @Override
    public String toString() {
        return "BalanceResponse{" +
                "merchantId=" + merchantId +
                ", total_paid=" + total_paid +
                ", total_pending=" + total_pending +
                ", total_rejected=" + total_rejected +
                '}';
    }
}
