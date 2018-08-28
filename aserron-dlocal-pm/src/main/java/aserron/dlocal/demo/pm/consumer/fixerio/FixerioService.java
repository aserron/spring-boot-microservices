package aserron.dlocal.demo.pm.consumer.fixerio;

import aserron.dlocal.demo.pm.PaymentApplication;
import java.math.BigDecimal;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aserron.dlocal.demo.pm.consumer.fixerio.FixerioConsumer;
import java.math.RoundingMode;
import java.time.Duration;
import org.springframework.web.client.RestClientException;

/**
 * Service implement FixerIO related functionality
 *
 * @author Andres
 */
@Service
public class FixerioService {

    private static Logger logger = LoggerFactory.getLogger(PaymentApplication.class);

    private static long MIN_LAST_CALL_SECONDS = 30;

    /**
     * Whenever FixerIO was ever successfully called
     */
    private boolean initialized = false;
    private Instant lastCall;
    private FixerioConsumer consumer;

    public FixerioService() {
    	FixerioConsumer fc = new FixerioConsumer();
        this.setConsumer(fc);
    }

    // Accessor
    private boolean isInitialized() {
        return initialized;
    }

    private void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    private Instant getLastCall() {
        return lastCall;
    }

    private void setLastCall(Instant lastCall) {
        this.lastCall = lastCall;
    }

    private FixerioConsumer getConsumer() {
        return consumer;
    }

    private void setConsumer(FixerioConsumer consumer) {
        this.consumer = consumer;
    }

    // functionallity
    // Implement: NotReadyException
    public BigDecimal convertCurrencyAmount(String currency,BigDecimal amount) 
            throws FixerioException 
    {
        BigDecimal result;
        try {
            updateFixerConsumer();
            result = convertCurrencyValueToUsd(currency, amount);
            return result;
        } catch (FixerioException e) {
            throw e;
        }
    }

    private void updateFixerConsumer() throws FixerioException {

        if (!isInitialized()) {
            initFixerConsumer();

        } else if (isReadyConsumer()) {
            refreshFixerConsumer();

        } else {
            throw new FixerioException(
                    FixerioException.FixerioExceptionReason.CONSUMER_NOT_READY,
                    this.getLastCall(),
                    "current time=" + Instant.now());
        }

    }

    private void initFixerConsumer() {
        refreshFixerConsumer();
        setInitialized(true);
    }

    private void refreshFixerConsumer() {
        getConsumer().getLatest();
        setLastCall(consumer.getResponse().getTimestamp());
    }

    // Mathematics
    /**
     * Get the currency rate for the given target currency.
     *
     * @param targetCurrency 3 letter fixer.io currency code
     * @return The current rate against USD.
     * @throws RuntimeException If parameters are not valid
     */
    private BigDecimal getCurrencyRate(String targetCurrency)
            throws FixerioException {

        @SuppressWarnings("unused")
		String euroCurrency= "EUR";
        
        String usdCurrency = "USD";

        BigDecimal euroToTargetRate;
        BigDecimal euroToUsdRate;
        BigDecimal targetToUsdRate;

        // get or throw ex
        FixerioResponse response = this.getConsumerResponse();

        // check or throw ex
        this.validateCurrency(response, targetCurrency);

        // All ok then perform the calculation
        euroToUsdRate = BigDecimal.valueOf(response.getRates().get(usdCurrency));
        euroToTargetRate = BigDecimal.valueOf(response.getRates().get(targetCurrency));

        // ratio for conversion
        // euroToTargetRate / euroToUsdRate;
        targetToUsdRate = euroToUsdRate.divide(euroToTargetRate, 3, RoundingMode.HALF_UP);

        /*
        // debug
        logger.debug("Convert from target currency to usd.");        
        logger.debug(" Euro to USD: {}", euroToUsdRate);
        logger.debug(" Euro to {}: {}", targetCurrency, euroToTargetRate);
        logger.info(" Result: {} to USD rate={}",targetCurrency, targetToUsdRate);
         */
        return targetToUsdRate;

    }

    // validation & inspection
    /**
     * Test is consumer is ready. Last Call minimal time lapse constrain must be
     * met. Prevent consumer abuse violation.
     *
     * @return True if consumer is ready for being used.
     */
    private boolean isReadyConsumer() {
        // check last call
        Duration len = Duration.between(getLastCall(),Instant.now());
        return (MIN_LAST_CALL_SECONDS <= len.getSeconds());
    }

    private void validateCurrency(FixerioResponse response, String currency)
            throws FixerioException {

        if (!response.getRates().containsKey(currency)) {
            throw new FixerioException(
                    FixerioException.FixerioExceptionReason.CURRENCY_NOT_FOUND,
                    this.getLastCall(),
                    "Target Currency=" + currency
            );
        }
    }

    // Implementation
    private FixerioResponse getConsumerResponse()
            throws FixerioException {

        try {
            return this.getConsumer().getLatest();

        } catch (RestClientException e) {
            throw new FixerioException(
                    FixerioException.FixerioExceptionReason.CONSUMER_REST_ERROR,
                    this.getLastCall(),
                    e.getMessage()
            );
        }
    }

    /**
     * Convert given currency amount to USD dollar. Use BigDecimal to correctly
     * operate money decimal units.
     *
     * Calculate the actual rate by making use of both currencies based on euro
     * rate.
     *
     * @param currency
     * @param amount
     * @return The converted amount or Zero if currency not found.
     */
    private BigDecimal convertCurrencyValueToUsd(String currency, BigDecimal amount) {

        BigDecimal rate = BigDecimal.ZERO;
        BigDecimal moneyAmount;
        
        try {
            rate = getCurrencyRate(currency);
        } catch (FixerioException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        moneyAmount = amount;

        return (rate.multiply(moneyAmount));
    }
}
