package aserron.dlocal.demo.pm.rest.exception;

public class IdempotencyRequestInFlightException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IdempotencyRequestInFlightException(String message) {
        super(message);
    }
}
