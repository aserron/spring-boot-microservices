package aserron.dlocal.demo.pm.data.service;

import aserron.dlocal.demo.pm.data.domain.IdempotencyRecord;
import java.util.UUID;

public class ClaimResult {

    public enum ClaimState {
        NEW,
        COMPLETED,
        IN_FLIGHT,
        MISMATCH,
        RETRIABLE_FAILED
    }

    private final ClaimState state;
    private final IdempotencyRecord record;

    public ClaimResult(ClaimState state, IdempotencyRecord record) {
        this.state = state;
        this.record = record;
    }

    public static ClaimResult newClaim(IdempotencyRecord record) {
        return new ClaimResult(ClaimState.NEW, record);
    }

    public static ClaimResult completed(IdempotencyRecord record) {
        return new ClaimResult(ClaimState.COMPLETED, record);
    }

    public static ClaimResult inFlight(IdempotencyRecord record) {
        return new ClaimResult(ClaimState.IN_FLIGHT, record);
    }

    public static ClaimResult mismatch(IdempotencyRecord record) {
        return new ClaimResult(ClaimState.MISMATCH, record);
    }

    public static ClaimResult retriableFailed(IdempotencyRecord record) {
        return new ClaimResult(ClaimState.RETRIABLE_FAILED, record);
    }

    public boolean isNew() {
        return state == ClaimState.NEW || state == ClaimState.RETRIABLE_FAILED;
    }

    public boolean isCompleted() {
        return state == ClaimState.COMPLETED;
    }

    public boolean isInFlight() {
        return state == ClaimState.IN_FLIGHT;
    }

    public boolean isMismatch() {
        return state == ClaimState.MISMATCH;
    }

    public ClaimState getState() {
        return state;
    }

    public IdempotencyRecord getRecord() {
        return record;
    }

    public UUID getRecordId() {
        return record != null ? record.getId() : null;
    }

    public Integer getResponseStatus() {
        return record != null ? record.getResponseStatus() : null;
    }

    public String getResponseBody() {
        return record != null ? record.getResponseBody() : null;
    }
}
