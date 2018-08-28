package aserron.dlocal.merchant.exc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;

public class ApiErrorResponse implements Serializable {

    private static final long serialVersionUID = -4477201818208576677L;

    
    /**
     * REST controller HTTP Header passed at exception time.
     */
    @JsonIgnore
    private HttpStatus httpStatus;    
    
    
    /**
     * HTTP status numeric code.
     */
    private int          status = 0;
    
    /**
     * HTTP  Description of the status.
     */
    private String       error = "";
    
    /**
     * Header HTTP cause.
     */
    private String      message= "";
    
    /**
     * Application error message or 
     *      string payloads,
     *      debug data,
     *      printed stack traces.
     */
    private String      detail= "";    
    
    
    /**
     * A list of previous error descriptions, usually strings.
     * Alternative stack trace could be served here.
     */
    private List<Object>errors;
    
    /**
     * Time of the exception.
     */
    private Instant timestamp; 
    
    
    // constructors
    
    public ApiErrorResponse() {
        super();
    }
    
    public ApiErrorResponse(HttpStatus status, String error_code , String message, String detail) {
        
        super();               
        this.httpStatus = status;
        
        this.status     = status.value();
        this.error      = error_code;
        this.message    = message;
        this.detail     = detail;
        
        this.errors     = Arrays.asList();
        
        this.timestamp  = Instant.now();
            
    }
    
    public ApiErrorResponse(HttpStatus status, String detail, List<Object> errors){
        
        super();               
        this.httpStatus = status;        
        this.status     = status.value();
        this.error      = status.name();        
        this.detail     = status.getReasonPhrase();
        
        this.message    = detail;
        
        this.errors     = errors;
        
    }

    
    
    // getters & setters
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    
    
}