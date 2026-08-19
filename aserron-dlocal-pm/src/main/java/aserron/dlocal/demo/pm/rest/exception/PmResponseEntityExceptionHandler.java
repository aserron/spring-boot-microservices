package aserron.dlocal.demo.pm.rest.exception;

import aserron.dlocal.demo.pm.data.service.SaleServiceException;
import aserron.dlocal.demo.pm.rest.controllers.MerchantNotFoundException;
import aserron.dlocal.demo.pm.rest.controllers.SaleNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PmResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage(ex.getMessage())
                .withDetail(ex.getLocalizedMessage())
                .build();
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String param = ex.getName();
        String msg = "Parameter '" + param + "' with value '" + ex.getValue() + "' is invalid";
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage(msg)
                .withDetail(ex.getLocalizedMessage())
                .build();
        return new ResponseEntity<>(response, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(HttpStatus.BAD_REQUEST.name())
                .withMessage("Validation failed")
                .withDetail(errors.toString())
                .build();

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Constraint violation",
                errors
        );

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MerchantNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleMerchantNotFound(MerchantNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage(ex.getMessage() != null ? ex.getMessage() : "Merchant not found")
                .withDetail("Merchant id does not exist")
                .build();

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler({SaleNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleSaleNotFound(SaleNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage(ex.getMessage() != null ? ex.getMessage() : "Sale not found")
                .withDetail("Sale id does not exist")
                .build();

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler({IdempotencyRequestInFlightException.class})
    public ResponseEntity<ApiErrorResponse> handleIdempotencyInFlight(IdempotencyRequestInFlightException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage(ex.getMessage() != null ? ex.getMessage() : "A matching request is currently in-flight")
                .withDetail("Request in progress, retry after delay")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Retry-After", "1");
        return new ResponseEntity<>(response, headers, status);
    }

    @ExceptionHandler({IdempotencyFingerprintMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleIdempotencyMismatch(IdempotencyFingerprintMismatchException ex) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage(ex.getMessage() != null ? ex.getMessage() : "Idempotency key payload mismatch")
                .withDetail("The idempotency key was reused with a different request payload")
                .build();

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler({MissingIdempotencyHeaderException.class})
    public ResponseEntity<ApiErrorResponse> handleMissingIdempotencyHeader(MissingIdempotencyHeaderException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage(ex.getMessage() != null ? ex.getMessage() : "Missing Idempotency-Key header")
                .withDetail("Idempotency-Key is required for this operation")
                .build();

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler({SaleServiceException.class})
    public ResponseEntity<ApiErrorResponse> handleServiceException(SaleServiceException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage(ex.getMessage())
                .withDetail(ex.getLocalizedMessage())
                .build();

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        String causeMsg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage("Database constraint violation")
                .withDetail(causeMsg)
                .build();

        return new ResponseEntity<>(response, new HttpHeaders(), status);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ApiErrorResponse> unknownException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String causeMsg = ex.getCause() != null ? ex.getCause().getLocalizedMessage() : ex.getLocalizedMessage();
        ApiErrorResponse response = ApiErrorResponseBuilder.anApiErrorResponse()
                .withStatus(status)
                .withError_code(status.name())
                .withMessage(ex.getMessage() != null ? ex.getMessage() : "Internal error")
                .withDetail(causeMsg)
                .build();

        return new ResponseEntity<>(response, new HttpHeaders(), status);
    }
}
