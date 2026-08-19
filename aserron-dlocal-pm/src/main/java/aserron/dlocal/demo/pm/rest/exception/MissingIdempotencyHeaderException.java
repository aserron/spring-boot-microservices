package aserron.dlocal.demo.pm.rest.exception;

public class MissingIdempotencyHeaderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MissingIdempotencyHeaderException(String message) {
        super(message);
    }
}
