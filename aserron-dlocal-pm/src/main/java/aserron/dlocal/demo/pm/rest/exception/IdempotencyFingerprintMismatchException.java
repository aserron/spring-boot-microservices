package aserron.dlocal.demo.pm.rest.exception;

public class IdempotencyFingerprintMismatchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IdempotencyFingerprintMismatchException(String message) {
        super(message);
    }
}
