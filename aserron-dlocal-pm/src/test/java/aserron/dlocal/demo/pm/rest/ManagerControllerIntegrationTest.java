package aserron.dlocal.demo.pm.rest;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.domain.TransactionStatus;
import aserron.dlocal.demo.pm.data.repositories.SaleRepository;
import aserron.dlocal.demo.pm.data.service.MerchantService;
import aserron.dlocal.demo.pm.data.service.TransactionJobService;
import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Date;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ManagerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private TransactionJobService transactionJobService;

    @MockBean
    private MerchantService merchantService;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        saleRepository.deleteAll();

        // Default mock behavior for merchant check: merchant 1 exists, merchant 999 does not
        Mockito.when(merchantService.getMerchantById(1L))
                .thenReturn(new ResponseEntity<>("{\"id\":1,\"name\":\"Test Merchant\"}", HttpStatus.OK));
        Mockito.when(merchantService.getMerchantById(999L))
                .thenThrow(new aserron.dlocal.demo.pm.rest.controllers.MerchantNotFoundException("999"));
    }

    @Test
    public void createSaleSuccess() throws Exception {
        CreateSaleRequest request = new CreateSaleRequest("EUR", new BigDecimal("100.00"), 501L, 1L);

        MvcResult result = mockMvc.perform(post("/pm/sale")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String saleId = objectMapper.readTree(responseJson).get("id").asText();

        Sale saved = saleRepository.findById(UUID.fromString(saleId)).orElse(null);
        assertNotNull(saved);
        assertEquals(TransactionStatus.PENDING, saved.getStatus());
        assertEquals("EUR", saved.getCurrency());
        assertEquals(1L, saved.getMerchantId().longValue());
        assertEquals(501L, saved.getTransactionId().longValue());
        assertNotNull(saved.getAmountUsd());
    }

    @Test
    public void createSaleIdempotency() throws Exception {
        CreateSaleRequest request = new CreateSaleRequest("USD", new BigDecimal("50.00"), 502L, 1L);

        // First call
        MvcResult firstResult = mockMvc.perform(post("/pm/sale")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String firstId = objectMapper.readTree(firstResult.getResponse().getContentAsString()).get("id").asText();

        // Second call with same tuple (merchant_id=1, transaction_id=502)
        MvcResult secondResult = mockMvc.perform(post("/pm/sale")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String secondId = objectMapper.readTree(secondResult.getResponse().getContentAsString()).get("id").asText();

        assertEquals("Idempotent requests must return the same sale ID", firstId, secondId);
        assertEquals(1, saleRepository.count());
    }

    @Test
    public void createSaleInvalidMerchant() throws Exception {
        CreateSaleRequest request = new CreateSaleRequest("USD", new BigDecimal("50.00"), 503L, 999L);

        mockMvc.perform(post("/pm/sale")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getSaleStatus() throws Exception {
        Sale sale = new Sale();
        sale.setMerchantId(1L);
        sale.setTransactionId(504L);
        sale.setCurrency("USD");
        sale.setAmountOrg(new BigDecimal("75.00"));
        sale.setAmountUsd(new BigDecimal("75.00"));
        sale.setStatus(TransactionStatus.PAID);
        sale.setCreated(new Date());
        Sale saved = saleRepository.save(sale);

        mockMvc.perform(get("/pm/status/" + saved.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().toString())))
                .andExpect(jsonPath("$.status", is("PAID")))
                .andExpect(jsonPath("$.merchant_id", is("1")))
                .andExpect(jsonPath("$.transaction_id", is("504")))
                .andExpect(jsonPath("$.amount_usd", is(75.00)))
                .andExpect(jsonPath("$.date", notNullValue()));
    }

    @Test
    public void getSaleStatusInvalidUuid() throws Exception {
        mockMvc.perform(get("/pm/status/not-a-valid-uuid")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBalance() throws Exception {
        Sale s1 = new Sale();
        s1.setMerchantId(1L);
        s1.setTransactionId(601L);
        s1.setCurrency("USD");
        s1.setAmountOrg(new BigDecimal("100.00"));
        s1.setAmountUsd(new BigDecimal("100.00"));
        s1.setStatus(TransactionStatus.PAID);
        s1.setCreated(new Date());
        saleRepository.save(s1);

        Sale s2 = new Sale();
        s2.setMerchantId(1L);
        s2.setTransactionId(602L);
        s2.setCurrency("USD");
        s2.setAmountOrg(new BigDecimal("50.00"));
        s2.setAmountUsd(new BigDecimal("50.00"));
        s2.setStatus(TransactionStatus.PENDING);
        s2.setCreated(new Date());
        saleRepository.save(s2);

        Sale s3 = new Sale();
        s3.setMerchantId(1L);
        s3.setTransactionId(603L);
        s3.setCurrency("USD");
        s3.setAmountOrg(new BigDecimal("25.00"));
        s3.setAmountUsd(new BigDecimal("25.00"));
        s3.setStatus(TransactionStatus.REJECTED);
        s3.setCreated(new Date());
        saleRepository.save(s3);

        mockMvc.perform(get("/pm/balance?merchant_id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_paid", is(100.00)))
                .andExpect(jsonPath("$.total_pending", is(50.00)))
                .andExpect(jsonPath("$.total_rejected", is(25.00)));
    }

    @Test
    public void batchJobProcessesPendingTransactions() {
        Sale s1 = new Sale();
        s1.setMerchantId(1L);
        s1.setTransactionId(701L);
        s1.setCurrency("USD");
        s1.setAmountOrg(new BigDecimal("10.00"));
        s1.setAmountUsd(new BigDecimal("10.00"));
        s1.setStatus(TransactionStatus.PENDING);
        s1.setCreated(new Date());
        saleRepository.save(s1);

        transactionJobService.processTransactions();

        Sale updated = saleRepository.findById(s1.getId()).orElse(null);
        assertNotNull(updated);
        assertNotNull(updated.getStatus());
        assertEquals(true, updated.getStatus() == TransactionStatus.PAID || updated.getStatus() == TransactionStatus.REJECTED);
    }
}
