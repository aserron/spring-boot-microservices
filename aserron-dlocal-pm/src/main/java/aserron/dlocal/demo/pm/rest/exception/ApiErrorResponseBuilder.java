package aserron.dlocal.demo.pm.rest.exception;

import org.springframework.http.HttpStatus;

public class ApiErrorResponseBuilder {

    public static ApiErrorResponseBuilder anApiErrorResponse() {
        return new ApiErrorResponseBuilder();
    }

    private HttpStatus status;
    private String error_code;
    private String message;
    private String detail;

    public ApiErrorResponseBuilder withStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ApiErrorResponseBuilder withError_code(String error_code) {
        this.error_code = error_code;
        return this;
    }

    public ApiErrorResponseBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public ApiErrorResponseBuilder withDetail(String detail) {
        this.detail = detail;
        return this;
    }

    /**
     * Builds a new api response object based on the builder configuration.
     * @return A new ApiErrorResponse instance.
     */
    public ApiErrorResponse build() {
        
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        
        apiErrorResponse.setStatus(this.status);
        apiErrorResponse.setError_code(this.error_code);
        apiErrorResponse.setDetail(this.detail);
        apiErrorResponse.setMessage(this.message);
        
        return apiErrorResponse;
    }
}
