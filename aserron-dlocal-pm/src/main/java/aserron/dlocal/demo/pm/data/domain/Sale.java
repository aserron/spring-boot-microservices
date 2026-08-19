package aserron.dlocal.demo.pm.data.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Validated
@Table(name = "sales", uniqueConstraints = {
    @UniqueConstraint(name = "uk_sales_merchant_tx", columnNames = {"merchants_id", "transaction_id"})
})
public class Sale implements Serializable {

    private static final long serialVersionUID = 5354508214418045835L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    @Positive
    @JsonProperty("merchant_id")
    @Column(name = "merchants_id")
    private Long merchantId;

    @NotBlank
    @Length(min = 3, max = 3)
    @Column(name = "currency")
    private String currency;

    @NotNull
    @Positive
    @Column(name = "amount_org")
    private BigDecimal amountOrg;

    @NotNull
    @Positive
    @Column(name = "amount_usd")
    private BigDecimal amountUsd;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    @NotNull
    @Positive
    @JsonProperty("transaction_id")
    @Column(name = "transaction_id")
    private Long transactionId;

    public Sale() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmountOrg() {
        return amountOrg;
    }

    public void setAmountOrg(BigDecimal amountOrg) {
        this.amountOrg = amountOrg;
    }

    public BigDecimal getAmountUsd() {
        return amountUsd;
    }

    public void setAmountUsd(BigDecimal amountUsd) {
        this.amountUsd = amountUsd;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
