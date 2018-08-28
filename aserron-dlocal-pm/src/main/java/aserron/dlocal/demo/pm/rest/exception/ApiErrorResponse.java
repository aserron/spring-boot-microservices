package aserron.dlocal.demo.pm.rest.exception;

import java.io.Serializable;
import org.springframework.http.HttpStatus;

/**
 * Ported Api Error Response DTO <br />
 * 
 * Creation is handled by an utility factory class 
 * {@link ApiErrorResponseBuilder} 
 * supporting fluent interface.
 * 
 * @see <a href="URL#https://www.javadevjournal.com/spring/exception-handling-for-rest-with-spring/">Referenced sources</a>
 * @author Andres
 */
public class ApiErrorResponse implements Serializable {

    private static final long serialVersionUID = 7813432568375629373L;
    
    private HttpStatus status;
    private String error_code;
    private String message;
    private String detail;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
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
    
}
