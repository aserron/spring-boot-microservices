package aserron.dlocal.demo.pm.data.service;

import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import java.time.Duration;
import java.util.UUID;

public interface IdempotencyService {

    /**
     * Attempts to claim an idempotency lease for the specified operation.
     *
     * @param clientId       the client/tenant identifier
     * @param operationName  the logical operation (e.g. "POST /pm/sale")
     * @param idempotencyKey the client-provided or computed idempotency key
     * @param requestHash    SHA-256 fingerprint of the request payload
     * @param ttl            time-to-live duration for record retention (defaults to 24h if null)
     * @return ClaimResult representing the claim state (NEW, COMPLETED, IN_FLIGHT, MISMATCH, RETRIABLE_FAILED)
     */
    ClaimResult claim(String clientId, String operationName, String idempotencyKey, String requestHash, Duration ttl);

    /**
     * Marks the record as COMPLETED and stores the cached response JSON.
     *
     * @param recordId       the primary key UUID of the IdempotencyRecord
     * @param httpStatus     the response HTTP status code
     * @param responseBody   the response JSON payload
     */
    void complete(UUID recordId, int httpStatus, String responseBody);

    /**
     * Marks the record as FAILED to allow subsequent retries.
     *
     * @param recordId the primary key UUID of the IdempotencyRecord
     */
    void fail(UUID recordId);

    /**
     * Computes the SHA-256 fingerprint from a CreateSaleRequest payload.
     *
     * @param request the create sale request DTO
     * @return SHA-256 hex string formatted as "sha256:<hex>"
     */
    String computeHash(CreateSaleRequest request);
}
