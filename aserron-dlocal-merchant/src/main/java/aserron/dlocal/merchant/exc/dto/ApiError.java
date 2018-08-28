/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.merchant.exc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;


public class ApiError implements Serializable {
    
    
    
    private static final long serialVersionUID = -1814355604537280471L;

    @JsonIgnore
    private HttpStatus     status;    
    
    private ApiErrorStatus response = new ApiErrorStatus();
    
    private String         action;
    private String         error;
    private String         message;
    
    private List<Object>   errors;
    

    public ApiError(HttpStatus status, String message, List<String> errors) {
        super();
        this.init(status, message, message, errors);
    }

    public ApiError(HttpStatus status, String message, String error) {
        super();
        this.init(status, error, message, new ArrayList<String>());
    }
    
    private void init (HttpStatus status, String error, 
                            String message, List<? extends Object> errors)
    {
        this.message = message;
        this.error   = error;
        this.errors  = new ArrayList<>(errors);
        
        this.setStatus(status);
    }

    // Accessors
    public HttpStatus getStatus() {
        return status;
    }

    private void setStatus (HttpStatus status){
        this.status          = status;
        
        response.setCode(status.value());
        response.setName(status.name());
        response.setReason(status.getReasonPhrase());
    }
    
}
