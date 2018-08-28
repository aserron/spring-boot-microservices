package aserron.dlocal.demo.pm.data.domain;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Validated
@Table(name = "sales")
public class Sale implements Serializable {

    private static final long serialVersionUID = 5354508214418045835L;
    
    
    
    @Id
    @NotNull
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Merchant identifier.
     */
    @NotNull
    @Positive
    @JsonProperty("merchant_id")
    @Column(name = "merchants_id")
    private Long merchantId;

    /**
     * 3 letter currency code conforming ISO specs.
     */
    @NotBlank
    @Length(min = 3, max = 3)
    private String currency;

    /**
     * Original currency money amount
     */
    @NotNull
    @Positive
    @Column(name = "amount_org")
    private BigDecimal amountOrg;
    
   
    /**
     * Money Amount In Usd Dollar
     */
    @NotNull
    @Positive
    @Column(name = "amount_usd")
    private BigDecimal amountUsd;

    /**
     * Sale's transaction status.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    /**
     * Sale's creation date. The time is stored in MySQL DateTime derived from
     * time stamp passed by
     */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    /**
     * Provided external id for transaction associated to the sale
     */
    @NotNull
    @Positive
    @JsonProperty("transaction_id")
    @Column(name = "transaction_id")
    private Long transactionId;

    // Accessors
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

    public void setTransactionId(Long transaction_id) {
        this.transactionId = transaction_id;
    }
}
