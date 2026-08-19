package aserron.dlocal.demo.pm.data.domain;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.validation.annotation.Validated;

@Entity
@Validated
@Table(name = "idempotency_records", uniqueConstraints = {
    @UniqueConstraint(name = "uk_idempotency_client_op_key", columnNames = {"client_id", "operation_name", "idempotency_key"})
})
public class IdempotencyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @NotBlank
    @Column(name = "client_id", nullable = false, length = 64)
    private String clientId;

    @NotBlank
    @Column(name = "operation_name", nullable = false, length = 128)
    private String operationName;

    @NotBlank
    @Column(name = "idempotency_key", nullable = false, length = 128)
    private String idempotencyKey;

    @NotBlank
    @Column(name = "request_hash", nullable = false, length = 71)
    private String requestHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private IdempotencyStatus status;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Lob
    @Column(name = "response_body")
    private String responseBody;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    public IdempotencyRecord() {
    }

    public static IdempotencyRecord createPending(
            String clientId,
            String operationName,
            String idempotencyKey,
            String requestHash,
            Duration ttl
    ) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setClientId(clientId);
        record.setOperationName(operationName);
        record.setIdempotencyKey(idempotencyKey);
        record.setRequestHash(requestHash);
        record.setStatus(IdempotencyStatus.PENDING);
        Instant now = Instant.now();
        record.setCreatedAt(Date.from(now));
        record.setExpiresAt(Date.from(now.plus(ttl != null ? ttl : Duration.ofHours(24))));
        return record;
    }

    public void markCompleted(int responseStatus, String responseBody) {
        this.status = IdempotencyStatus.COMPLETED;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
    }

    public void markFailed() {
        this.status = IdempotencyStatus.FAILED;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }

    public IdempotencyStatus getStatus() {
        return status;
    }

    public void setStatus(IdempotencyStatus status) {
        this.status = status;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
