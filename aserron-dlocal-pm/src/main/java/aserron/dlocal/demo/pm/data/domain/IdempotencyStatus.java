package aserron.dlocal.demo.pm.data.domain;

/**
 * Lifecycle states for an IdempotencyRecord:
 * - PENDING: Request received and lease claimed; processing is in-flight.
 * - COMPLETED: Request finished successfully; response is cached for replay.
 * - FAILED: Processing encountered an error; retries are permitted.
 */
public enum IdempotencyStatus {
    PENDING,
    COMPLETED,
    FAILED
}
