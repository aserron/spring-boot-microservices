package aserron.dlocal.demo.pm.consumer.fixerio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 * BDD Unit Test for FixerioService.
 * Pure unit test (Zero Spring context, fast execution).
 */
public class FixerioServiceTest {

    private FixerioConsumer fixerioConsumer;
    private FixerioService fixerioService;

    @Before
    public void setUp() {
        fixerioConsumer = mock(FixerioConsumer.class);
        fixerioService = new FixerioService(fixerioConsumer);
    }

    @Test
    public void givenUsdCurrency_whenConvertCurrencyAmount_thenReturnExactSameAmount() {
        // Given
        String currency = "USD";
        BigDecimal amount = new BigDecimal("150.00");

        // When
        BigDecimal amountUsd = fixerioService.convertCurrencyAmount(currency, amount);

        // Then
        assertThat(amountUsd).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    public void givenEurBaseCurrency_whenConvertCurrencyAmount_thenMultiplyByUsdRate() {
        // Given
        FixerioResponse response = new FixerioResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.20);
        rates.put("EUR", 1.00);
        response.setRates(rates);

        given(fixerioConsumer.getLatest()).willReturn(response);

        // When (converting 100 EUR -> USD)
        BigDecimal amountUsd = fixerioService.convertCurrencyAmount("EUR", new BigDecimal("100.00"));

        // Then: 100 * (1.20 / 1.00) = 120.00 USD
        assertThat(amountUsd).isEqualByComparingTo(new BigDecimal("120.00"));
        then(fixerioConsumer).should(times(1)).getLatest();
    }

    @Test
    public void givenForeignCurrency_whenConvertCurrencyAmount_thenTriangulateViaEurCrossRate() {
        // Given
        FixerioResponse response = new FixerioResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.20);
        rates.put("GBP", 0.80);
        response.setRates(rates);

        given(fixerioConsumer.getLatest()).willReturn(response);

        // When (converting 100 GBP -> USD)
        // 100 * (1.20 / 0.80) = 150.00 USD
        BigDecimal amountUsd = fixerioService.convertCurrencyAmount("GBP", new BigDecimal("100.00"));

        // Then
        assertThat(amountUsd).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    public void givenMultipleRequestsWithin30Seconds_whenConvertCurrencyAmount_thenServeFromCache() {
        // Given
        FixerioResponse response = new FixerioResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.15);
        rates.put("UYU", 45.00);
        response.setRates(rates);

        given(fixerioConsumer.getLatest()).willReturn(response);

        // When (3 consecutive conversions within sub-30s window)
        BigDecimal call1 = fixerioService.convertCurrencyAmount("UYU", new BigDecimal("450.00"));
        BigDecimal call2 = fixerioService.convertCurrencyAmount("UYU", new BigDecimal("900.00"));
        BigDecimal call3 = fixerioService.convertCurrencyAmount("UYU", new BigDecimal("1350.00"));

        // Then: Consumer must be invoked only once due to 30s TTL cache
        then(fixerioConsumer).should(times(1)).getLatest();
        assertThat(call1).isEqualByComparingTo(new BigDecimal("11.50"));
        assertThat(call2).isEqualByComparingTo(new BigDecimal("23.00"));
        assertThat(call3).isEqualByComparingTo(new BigDecimal("34.50"));
    }

    @Test
    public void givenConsumerThrowsException_whenConvertCurrencyAmount_thenFallbackToDefaultRates() {
        // Given
        given(fixerioConsumer.getLatest()).willThrow(new RuntimeException("Fixer.io Network Timeout"));

        // When
        BigDecimal amountUsd = fixerioService.convertCurrencyAmount("EUR", new BigDecimal("100.00"));

        // Then: Must not crash, should use default rates (EUR rate 1.0, USD rate 1.15)
        assertThat(amountUsd).isNotNull();
        assertThat(amountUsd).isGreaterThan(BigDecimal.ZERO);
    }
}
