package aserron.dlocal.merchant.exc.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Null;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class ApiErrorResponseBuilder {
    
    // Factory methods
    
    public static ApiErrorResponse create(HttpStatus status, Exception ex,@Nullable List<? super Object> errors){
        
        List<? super Object> errorList;
        
        if(errors == null){    
            
            errorList = new ArrayList<>();                        
            errorList.addAll(stackTraceToList(ex));
            
            errors = errorList;
        }
        
        ApiErrorResponse response;
        response = ApiErrorResponseBuilder.anApiErrorResponse()
                
                .withHttpStatus(status)
                
                .withStatus   (status.value()+"")
                .withError    (status.name())
                .withDetail   (status.getReasonPhrase())
                .withMessage  (ex.getLocalizedMessage())
                .withErrorList(errors)
                .build();
        
        return response;
    }

    /**
     * Build a REST API Response from REST Controller Advice handler args.
     * Return type cast to Object, matching with the handler's signature.
     *
     * Implements a "generic" object creation compatible with serialization.
     *
     * @param status
     * @param ex
     * @param message
     * @return An Object API Response.
     */
    public static Map<String, Object> fromEx(HttpStatus status,
            Exception ex, String message) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("exception", ex.toString());

        Throwable cause = ex.getCause();
        if (cause != null) {
            body.put("exceptionCause", ex.getCause().toString());
        }
        return body;
    }
    
    // utils
    public static List<Object> stackTraceToList(Exception ex ){
        
        List<Object> errors;        
        errors = new ArrayList<>();        
        Arrays.stream(ex.getStackTrace())
                .forEach((t) -> {
                    errors.add(t);                    
                });        
        return errors;
    }
    
    public static List<Object> exSupressedToList(Exception ex ){        
        List<Object> errors;        
        errors = new ArrayList<>();        
        Arrays.stream(ex.getSuppressed())
                .forEach((t) -> {
                    errors.add(t);                    
                });        
        return errors;
    }
    
    
    
    
    // Instance properteis
        
    /**
     * HTTP Status object
     */
    private HttpStatus htttpStatus;
    
    /**
     * HTTP Status code
     */
    private String status;
    
    /**
     * HTTP Status readeable code.
     */
    private String error;
    
    /**
     * HTTP Status message
     */
    private String message;
    
    /**
     * HTTP Status message or
     *      customized message
     */
    private String detail;
    
    
    /**
     * List of error descriptions or
     *      stack traces
     *      dumps
     */
    private List<Object> errors;

    
    /**
     * Hide constructor and enforce configuration by methods.
     * Builder will configure itself then build the 
     * response instance
     */
    private  ApiErrorResponseBuilder() {
        
        this.status = "";
        this.error  = "";
        this.detail = "";
        
        this.errors = new ArrayList<>();
    }
    
    
    // Fluent interface factory methods.
    
    /**
     * Api Builder Factory.
     * @return A new Api Error Builder instance.
     */
    public static ApiErrorResponseBuilder anApiErrorResponse() {
        return new ApiErrorResponseBuilder();
    }
    
    public ApiErrorResponseBuilder withHttpStatus(HttpStatus status) {
        this.htttpStatus = status;
        return this;
    }
    
    public ApiErrorResponseBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public ApiErrorResponseBuilder withError(String error) {
        this.error = error;
        return this;
    }

    public ApiErrorResponseBuilder withMessage(String reason) {
        this.message = reason;
        return this;
    }

    public ApiErrorResponseBuilder withDetail(String exDetails) {
        this.detail = exDetails;
        return this;
    }
    
    public ApiErrorResponseBuilder withErrorList (List<Object> exErrorList){
        this.errors = exErrorList;
        return this;
    }
    
    
    /**
     * Builds a new api response object based on the builder configuration.
     * @return A new ApiErrorResponse instance.
     * 
     * @todo Add null check before build.
     */
    public ApiErrorResponse build() {
        

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        
        apiErrorResponse.setHttpStatus(this.htttpStatus);
        apiErrorResponse.setError(this.error);
        apiErrorResponse.setDetail(this.detail);
        apiErrorResponse.setMessage(this.message);

        return apiErrorResponse;
    }
    
    
    
    // setters & getters
    
    public void setErrors(List<Object> errorList){
        this.errors.clear();
        this.errors.addAll(errorList);
    }

}
