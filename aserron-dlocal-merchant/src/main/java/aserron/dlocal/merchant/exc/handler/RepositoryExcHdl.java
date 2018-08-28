/*
 * Response Entity Exception Hanlder
 */
package aserron.dlocal.merchant.exc.handler;

import aserron.dlocal.merchant.exc.dto.ApiErrorResponse;
import aserron.dlocal.merchant.exc.dto.ApiErrorResponseBuilder;
import aserron.dlocal.merchant.MerchantRestApplication;


import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;

import aserron.dlocal.merchant.rest.MerchantNotFoundException;


import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @todo Assignar este control solo al paquete de repositorios.
 * @author Andres
 */
@RestControllerAdvice(basePackages = "aserron.dlocal.merchant")       
public class RepositoryExcHdl extends ResponseEntityExceptionHandler {

    
    
    @ExceptionHandler(value = {
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    public ResponseEntity<Object> handleConflict(RuntimeException ex,
                                                        WebRequest request) 
    {
        List<Object>      errors;
        ApiErrorResponse    body;
        
        HttpStatus   status = HttpStatus.CONFLICT;
        HttpHeaders  heads  = new HttpHeaders();        
        
        errors = ApiErrorResponseBuilder.exSupressedToList(ex);
        body   = ApiErrorResponseBuilder.create(status,ex,errors);
        
        return handleExceptionInternal(ex, body, heads, status, request);
    }

    
    
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) 
    {
        
        ApiErrorResponse response;        
        List<Object> errorMessages;                
        String pattern;
        
        pattern       = "Field error in object '%s' on field '%s': rejected value [%s].";        
        errorMessages = new ArrayList<>();
        
        ex.getBindingResult().getAllErrors()
                .stream()
                .forEach((ObjectError t) -> {
                    if(t instanceof FieldError){                        
                        final String msg = String.format(
                                        pattern, 
                                        t.getObjectName(),
                                        ((FieldError) t).getField(),
                                        ((FieldError) t).getRejectedValue()
                                    );
                        
                        errorMessages.add(msg);
                        
                    } else {                    
                        errorMessages.add(t.toString());
                    }                    
                 });
        
        response = ApiErrorResponseBuilder.create(status, ex, errorMessages);
        
        logger.warn("handleMethodArgumentNotValid");
        logger.debug(response);
        
        return ResponseEntity.status(status).headers(headers).body(response);
        
    }
    
    
    
    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
                                               MethodArgumentTypeMismatchException ex, 
                                               WebRequest request)
    { 
        
        List<Object>     errors    = ApiErrorResponseBuilder.exSupressedToList(ex);
        HttpStatus       newStatus = HttpStatus.BAD_REQUEST;
        ApiErrorResponse response  = ApiErrorResponseBuilder.create(
                                                newStatus, 
                                                ex, 
                                                errors );
        
        response.setDetail(buildMethodArgumentTypeMismatchMsg(ex));
        
        return handleExceptionInternal(ex, response, new HttpHeaders(), newStatus, request);

    }
    
    /**
     * MA TypeMismatch Msg Builder.
     * @param ex 
     * @return The built message based on the pattern and ex data.
     */
    private String buildMethodArgumentTypeMismatchMsg (MethodArgumentTypeMismatchException ex){
        
        return ex.getParameter().getParameterName()
                    + " should be of type " 
                    + ex.getParameter().getParameterType()
                    + " for method:" 
                    + ex.getName();
    }
    
    
    /**
     * ConstraintViolationException
     * @param ex
     * @param request
     * @return 
     */
    @ExceptionHandler({
        ConstraintViolationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) 
    {
        ApiErrorResponse response;
        HttpStatus       status;
        
        
        List<? super Object> errors;
        
        errors = new ArrayList<>();
        
        
        ex.getConstraintViolations().stream().forEach((t) -> {
            
            errors.add(String.format(
                                "%1$s -> %2$s : %3$s",
                                t.getRootBeanClass().getName(),
                                t.getPropertyPath(),
                                t.getMessage()
                            ));
        });
        
        status      = HttpStatus.BAD_REQUEST;
        response    = ApiErrorResponseBuilder.create(status, ex, errors);
         
        return ResponseEntity.status(status).body(response);
    }

    

    // Merchant Exceptions
    @ExceptionHandler({
        MerchantNotFoundException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleMerchantError(MerchantNotFoundException ex, HttpServletResponse response) {

        HttpStatus status    = HttpStatus.NOT_FOUND;        
        ApiErrorResponse res = ApiErrorResponseBuilder.create(status, ex, null);
        
        return new ResponseEntity<>(res,new HttpHeaders(),status);
    }

    
    
    /**
     * The last handler in this layer set the last handler as "final"
     * They finalize their working Re-Throwing the unkown exception.
     * It's expected to be a high level error.
     * 
     * The cycle could be somehow distorted but they leave a clue with this 
     * last handling, this show that you should "handle with care" and try 
     * to re use the underlying structures.
     * 
     * That's the example of the TypeMismatch that came already worked to 
     * output the indicated message about the types, no work or extra process
     * involved.
     * 
     * @param exception Unknown Exception re-thrown from the framework error handling.
     * @param request
     * @return The HTTP response, a json payload could came with extra info.
     */
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)    
    @ExceptionHandler({
            Exception.class
    })        
    public ResponseEntity<Object> unkwonHandleException(Exception exception, @Nullable WebRequest request) 
    {
        logger.info(">>>> Exc Handlerer: Catch All Method:" + exception.getMessage());
        
        
        ResponseEntity<Object> response;
        
        List<Object>     errors;
        
        ApiErrorResponse body;
        ApiErrorResponse resObj;
        
        HttpHeaders      headers;
        HttpStatus       status;
        
        // this will never happpen, the exception is a catch all clause.
        headers  = (new HttpHeaders());
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        status   = HttpStatus.INTERNAL_SERVER_ERROR;
        
        response = ResponseEntity
                        .status(status)
                        .body(exception);
        
        try {            
            // try to resolve the ex and get the response
            response = handleException(exception, request);
            
        } catch (Exception exc) {
            
            // e: is the same as "ex", as we explain before...                        
            logger.warn("RestMvc Unkown Error: " + exc.getLocalizedMessage());
            
            body = ApiErrorResponseBuilder.create(status, exc, 
                                ApiErrorResponseBuilder.stackTraceToList(exception));
            
            response = ResponseEntity
                        .status(status)
                        .headers(headers)                        
                        .body(body);
        }
        
        
            
        // we get the config for the dto with either outcome.

        if(MerchantRestApplication.DEBUG_MODE){
            
            errors = ApiErrorResponseBuilder.stackTraceToList(exception);
            resObj = ApiErrorResponseBuilder.create(status, exception, errors);

            return new ResponseEntity<>(resObj, headers, status);
            
        } else {
            
            // an empty response. with same heads
            return ResponseEntity.status(status).headers(headers).build();
        }
    }    
}



/*
    // revisar
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                                HttpHeaders headers, 
                                                                HttpStatus status, 
                                                                WebRequest request)
    {
        HttpStatus       newStatus = HttpStatus.BAD_REQUEST;
        ApiErrorResponse response  = ApiErrorResponseBuilder.create(newStatus, ex, buildMismatchError(ex));
        
    }
*/