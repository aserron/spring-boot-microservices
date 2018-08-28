/*
 * Response Entity Exception Hanlder
 */
package aserron.dlocal.demo.pm.rest.exception;

import aserron.dlocal.demo.pm.data.service.SaleServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.aspectj.util.LangUtil;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Component
public class PmResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {

        // String bodyResponse;
        HttpHeaders headers;

        headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        // bodyResponse = "{\"msg\":\"testing output\",\"ex\":\""+ ex.toString() +"\"}";
        return handleExceptionInternal(ex,
                // bodyResponse, 
                ex,
                headers,
                HttpStatus.CONFLICT, request);
    }

    @Override
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {

        ApiErrorResponse response;

        response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(HttpStatus.BAD_REQUEST.name())
                .withMessage(ex.getLocalizedMessage())
                .build();

        return new ResponseEntity<>(response, response.getStatus());
    }

    // Respository Constrain Violations
    @ExceptionHandler({RepositoryConstraintViolationException.class})
    public ResponseEntity<Object> handleAccessDeniedException(
            RuntimeException ex, WebRequest request) {

        RepositoryConstraintViolationException nevEx;

        nevEx = (RepositoryConstraintViolationException) ex;

        String errors = nevEx.getErrors().getAllErrors()
                .stream()
                .map(p -> p.toString())
                .collect(Collectors.joining("\n"));

        return new ResponseEntity<>(errors,
                new HttpHeaders(),
                HttpStatus.PARTIAL_CONTENT);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
                                        ConstraintViolationException ex, 
                                        WebRequest request) 
    {
        List<String> errors = new ArrayList<String>();
        
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() 
                    + " " + violation.getPropertyPath() 
                    + ": " + violation.getMessage()
            );
        }
        
        
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST, 
                ex.getLocalizedMessage(), 
                errors
            );
        
        
        
        return new ResponseEntity<Object>(
                        apiError, 
                        new HttpHeaders(), 
                        apiError.getStatus()
                    );
    }
    
    
    // Sale Service
    @ExceptionHandler({SaleServiceException.class})    
    public ResponseEntity<ApiErrorResponse> handleServiceException(Exception ex){
        
        SaleServiceException saleEx = (SaleServiceException) ex;
        
        ApiErrorResponse response;
        HttpStatus status = HttpStatus.BAD_REQUEST;

        response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(String.valueOf(status.value()))
                .withMessage(status.getReasonPhrase())
                .withDetail(saleEx.toString())
                .build();
        
        return new ResponseEntity<>(response, response.getStatus());
        
    }
    

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiErrorResponse> unknownException(Exception ex) {
    	ApiErrorResponse response;
    	
    	response = ApiErrorResponseBuilder.anApiErrorResponse()
    			.withMessage(ex.getMessage())
    			.withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    			.withDetail(ex.getCause().getLocalizedMessage())
    			.build();
        
        return new ResponseEntity<>(response, 
        		new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
