package aserron.dlocal.demo.pm.rest.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateSaleRequest implements Serializable {

    private static final long serialVersionUID = -3538551675304923702L;

    @NotBlank(message = "currency is required")
    private String currency;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "transaction_id is required")
    private Long transaction_id;

    @NotNull(message = "merchant_id is required")
    private Long merchant_id;

    public CreateSaleRequest() {
    }

    public CreateSaleRequest(String currency, BigDecimal amount, Long transaction_id, Long merchant_id) {
        this.currency = currency;
        this.amount = amount;
        this.transaction_id = transaction_id;
        this.merchant_id = merchant_id;
    }

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

    @JsonSetter("transaction_id")
    public void setTransaction_idFromString(Object val) {
        if (val instanceof Number) {
            this.transaction_id = ((Number) val).longValue();
        } else if (val != null) {
            try {
                this.transaction_id = Long.parseLong(val.toString().trim());
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public Long getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(Long merchant_id) {
        this.merchant_id = merchant_id;
    }

    @JsonSetter("merchant_id")
    public void setMerchant_idFromString(Object val) {
        if (val instanceof Number) {
            this.merchant_id = ((Number) val).longValue();
        } else if (val != null) {
            try {
                this.merchant_id = Long.parseLong(val.toString().trim());
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
