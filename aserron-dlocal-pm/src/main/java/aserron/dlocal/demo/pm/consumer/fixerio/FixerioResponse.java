package aserron.dlocal.demo.pm.consumer.fixerio;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FixerioResponse {

    private String base = "EUR";

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant timestamp = Instant.now();

    private String date;
    private String success;

    private Map<String, Double> rates = new HashMap<>();

    public FixerioResponse() {
        initDefaultRates();
    }

    public void initDefaultRates() {
        this.base = "EUR";
        this.timestamp = Instant.now();
        this.success = "true";
        if (this.rates == null) {
            this.rates = new HashMap<>();
        }
        // Common default rates vs EUR base
        this.rates.put("EUR", 1.0);
        this.rates.put("USD", 1.15);
        this.rates.put("GBP", 0.88);
        this.rates.put("BRL", 4.50);
        this.rates.put("UYU", 35.0);
        this.rates.put("ARS", 30.0);
        this.rates.put("MXN", 22.0);
        this.rates.put("CLP", 750.0);
        this.rates.put("COP", 3400.0);
        this.rates.put("PEN", 3.80);
        this.rates.put("CNY", 7.80);
        this.rates.put("JPY", 130.0);
        this.rates.put("CAD", 1.50);
        this.rates.put("AUD", 1.60);
        this.rates.put("ANG", 2.05);
        this.rates.put("FJD", 2.45);
        this.rates.put("TMT", 4.00);
        this.rates.put("UAH", 31.0);
        this.rates.put("XDR", 0.82);
        this.rates.put("CRC", 650.0);
        this.rates.put("XOF", 655.957);
        this.rates.put("SZL", 16.0);
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
