package aserron.dlocal.demo.pm.rest;

import aserron.dlocal.demo.pm.data.domain.IdempotencyRecord;
import aserron.dlocal.demo.pm.data.domain.IdempotencyStatus;
import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import aserron.dlocal.demo.pm.data.repositories.IdempotencyRecordRepository;
import aserron.dlocal.demo.pm.data.repositories.SaleRepository;
import aserron.dlocal.demo.pm.data.service.IdempotencyCleanupJob;
import aserron.dlocal.demo.pm.data.service.IdempotencyService;
import aserron.dlocal.demo.pm.data.service.MerchantService;
import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IdempotencyRecordIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private IdempotencyCleanupJob idempotencyCleanupJob;

    @MockBean
    private MerchantService merchantService;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        saleRepository.deleteAll();
        idempotencyRecordRepository.deleteAll();

        // Default mock behavior for merchant check
        Mockito.when(merchantService.getMerchantById(1L))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"name\":\"Test Merchant\"}", HttpStatus.OK));
    }

    @Test
    public void createSaleWithIdempotencyKeyReplay() throws Exception {
        String key = "pay-key-" + UUID.randomUUID();
        CreateSaleRequest request = new CreateSaleRequest("EUR", new BigDecimal("100.00"), 1001L, 1L);

        // 1. First Execution
        MvcResult firstResult = mockMvc.perform(post("/pm/sale")
                .header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        String firstId = objectMapper.readTree(firstResult.getResponse().getContentAsString()).get("id").asText();

        // Verify IdempotencyRecord in DB
        Optional<IdempotencyRecord> recordOpt = idempotencyRecordRepository
                .findByClientIdAndOperationNameAndIdempotencyKey("1", "POST /pm/sale", key);
        assertTrue(recordOpt.isPresent());
        IdempotencyRecord record = recordOpt.get();
        assertEquals(IdempotencyStatus.COMPLETED, record.getStatus());
        assertEquals(Integer.valueOf(200), record.getResponseStatus());
        assertNotNull(record.getResponseBody());
        assertTrue(record.getResponseBody().contains(firstId));

        // 2. Replay with identical key & payload
        MvcResult secondResult = mockMvc.perform(post("/pm/sale")
                .header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String secondId = objectMapper.readTree(secondResult.getResponse().getContentAsString()).get("id").asText();

        assertEquals("Replayed request must return the exact same sale UUID", firstId, secondId);
        assertEquals(1, saleRepository.count());
        assertEquals(1, idempotencyRecordRepository.count());
    }

    @Test
    public void createSaleWithPayloadMismatchReturns422() throws Exception {
        String key = "pay-key-mismatch-" + UUID.randomUUID();
        CreateSaleRequest initialRequest = new CreateSaleRequest("USD", new BigDecimal("50.00"), 1002L, 1L);

        // 1. Initial Request
        mockMvc.perform(post("/pm/sale")
                .header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(initialRequest)))
                .andExpect(status().isOk());

        // 2. Reused key with altered amount ($50.00 -> $500.00)
        CreateSaleRequest alteredRequest = new CreateSaleRequest("USD", new BigDecimal("500.00"), 1002L, 1L);

        mockMvc.perform(post("/pm/sale")
                .header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alteredRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error_code", is("UNPROCESSABLE_ENTITY")));

        // Ensure database state is unaltered
        assertEquals(1, saleRepository.count());
    }

    @Test
    public void createSaleInFlightConflictReturns409() throws Exception {
        String key = "pay-key-inflight-" + UUID.randomUUID();
        CreateSaleRequest request = new CreateSaleRequest("USD", new BigDecimal("75.00"), 1003L, 1L);
        String hash = idempotencyService.computeHash(request);

        // Pre-insert an in-flight PENDING record
        IdempotencyRecord pendingRecord = IdempotencyRecord.createPending("1", "POST /pm/sale", key, hash, Duration.ofHours(24));
        idempotencyRecordRepository.saveAndFlush(pendingRecord);

        // Concurrent/second request arrives while first is PENDING
        mockMvc.perform(post("/pm/sale")
                .header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(header().string("Retry-After", "1"))
                .andExpect(jsonPath("$.error_code", is("CONFLICT")));
    }

    @Test
    public void createSaleFailureStateAllowsRetry() throws Exception {
        String key = "pay-key-failed-" + UUID.randomUUID();
        CreateSaleRequest request = new CreateSaleRequest("USD", new BigDecimal("90.00"), 1004L, 1L);
        String hash = idempotencyService.computeHash(request);

        // Pre-insert a FAILED record from a previous failed attempt
        IdempotencyRecord failedRecord = IdempotencyRecord.createPending("1", "POST /pm/sale", key, hash, Duration.ofHours(24));
        failedRecord.markFailed();
        idempotencyRecordRepository.saveAndFlush(failedRecord);

        // Subsequent retry should succeed and transition state to COMPLETED
        mockMvc.perform(post("/pm/sale")
                .header("Idempotency-Key", key)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));

        IdempotencyRecord updated = idempotencyRecordRepository
                .findByClientIdAndOperationNameAndIdempotencyKey("1", "POST /pm/sale", key).orElse(null);
        assertNotNull(updated);
        assertEquals(IdempotencyStatus.COMPLETED, updated.getStatus());
        assertEquals(1, saleRepository.count());
    }

    @Test
    public void scheduledCleanupPurgesExpiredCompletedRecords() {
        Instant now = Instant.now();

        // 1. Expired COMPLETED record (expires_at = 1 hour ago)
        IdempotencyRecord expiredCompleted = IdempotencyRecord.createPending("1", "POST /pm/sale", "exp-comp-1", "hash1", Duration.ofHours(24));
        expiredCompleted.setExpiresAt(Date.from(now.minus(Duration.ofHours(1))));
        expiredCompleted.markCompleted(200, "{\"id\":\"111\"}");
        idempotencyRecordRepository.save(expiredCompleted);

        // 2. Expired PENDING record (expires_at = 1 hour ago) - Should NOT be deleted
        IdempotencyRecord expiredPending = IdempotencyRecord.createPending("1", "POST /pm/sale", "exp-pend-2", "hash2", Duration.ofHours(24));
        expiredPending.setExpiresAt(Date.from(now.minus(Duration.ofHours(1))));
        idempotencyRecordRepository.save(expiredPending);

        // 3. Active COMPLETED record (expires_at = 23 hours in future) - Should NOT be deleted
        IdempotencyRecord activeCompleted = IdempotencyRecord.createPending("1", "POST /pm/sale", "act-comp-3", "hash3", Duration.ofHours(24));
        activeCompleted.setExpiresAt(Date.from(now.plus(Duration.ofHours(23))));
        activeCompleted.markCompleted(200, "{\"id\":\"333\"}");
        idempotencyRecordRepository.save(activeCompleted);

        assertEquals(3, idempotencyRecordRepository.count());

        // Execute scheduled cleanup job
        idempotencyCleanupJob.purgeExpiredRecords();

        assertEquals(2, idempotencyRecordRepository.count());
        assertTrue(idempotencyRecordRepository.findByClientIdAndOperationNameAndIdempotencyKey("1", "POST /pm/sale", "exp-comp-1").isEmpty());
        assertTrue(idempotencyRecordRepository.findByClientIdAndOperationNameAndIdempotencyKey("1", "POST /pm/sale", "exp-pend-2").isPresent());
        assertTrue(idempotencyRecordRepository.findByClientIdAndOperationNameAndIdempotencyKey("1", "POST /pm/sale", "act-comp-3").isPresent());
    }
}
