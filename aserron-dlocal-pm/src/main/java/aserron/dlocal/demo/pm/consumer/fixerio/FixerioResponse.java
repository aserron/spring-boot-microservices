
package aserron.dlocal.demo.pm.consumer.fixerio;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.Map;

/**
 *
 * @author Andres
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class FixerioResponse {    
    
    private String base;
    
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant timestamp;
    
    private String date;
    private String success;    
    
    private Map<String, Double> rates;

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
