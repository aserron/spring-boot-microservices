package aserron.dlocal.demo.pm.rest.dto;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class StatusResponse {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static StatusResponse createFrom(Sale sale) {
        SimpleDateFormat dt = new SimpleDateFormat(DATE_FORMAT);
        StatusResponse response = new StatusResponse();

        response.setId(sale.getId() != null ? sale.getId().toString() : null);
        response.setMerchant_id(sale.getMerchantId() != null ? sale.getMerchantId().toString() : null);
        response.setTransaction_id(sale.getTransactionId() != null ? sale.getTransactionId().toString() : null);
        response.setAmount_usd(sale.getAmountUsd());
        response.setDate(sale.getCreated() != null ? dt.format(sale.getCreated()) : null);
        response.setStatus(sale.getStatus());

        return response;
    }

    private String id;
    private String date;
    private String merchant_id;
    private String transaction_id;
    private BigDecimal amount_usd;
    private TransactionStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public BigDecimal getAmount_usd() {
        return amount_usd;
    }

    public void setAmount_usd(BigDecimal amount_usd) {
        this.amount_usd = amount_usd;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
