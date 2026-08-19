package aserron.dlocal.demo.pm.consumer.fixerio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FixerioService {

    private static final Logger logger = LoggerFactory.getLogger(FixerioService.class);
    private static final long MIN_LAST_CALL_SECONDS = 30;

    private boolean initialized = false;
    private Instant lastCall = Instant.EPOCH;
    private FixerioConsumer consumer;
    private FixerioResponse latestResponse;

    @Autowired
    public FixerioService(FixerioConsumer consumer) {
        this.consumer = consumer;
    }

    public FixerioService() {
        this.consumer = new FixerioConsumer();
    }

    public synchronized BigDecimal convertCurrencyAmount(String currency, BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }

        if (currency == null || currency.trim().equalsIgnoreCase("USD")) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        updateFixerConsumer();
        return convertCurrencyValueToUsd(currency.trim().toUpperCase(), amount);
    }

    private synchronized void updateFixerConsumer() {
        Instant now = Instant.now();
        if (!initialized || isReadyConsumer(now)) {
            try {
                FixerioResponse res = consumer.getLatest();
                if (res != null && res.getRates() != null && !res.getRates().isEmpty()) {
                    this.latestResponse = res;
                }
                lastCall = now;
                initialized = true;
            } catch (Exception e) {
                logger.warn("Could not refresh Fixer rates: {}", e.getMessage());
                if (!initialized) {
                    initialized = true;
                }
            }
        }
    }

    private boolean isReadyConsumer(Instant now) {
        Duration duration = Duration.between(lastCall, now);
        return duration.getSeconds() >= MIN_LAST_CALL_SECONDS;
    }

    public BigDecimal getCurrencyRate(String targetCurrency) {
        if ("USD".equalsIgnoreCase(targetCurrency)) {
            return BigDecimal.ONE;
        }

        FixerioResponse response = this.latestResponse != null ? this.latestResponse : consumer.getResponse();
        if (response == null || response.getRates() == null) {
            return BigDecimal.ONE;
        }

        Double euroToUsd = response.getRates().get("USD");
        Double euroToTarget = response.getRates().get(targetCurrency.toUpperCase());

        if (euroToUsd == null || euroToUsd == 0) {
            euroToUsd = 1.15;
        }

        if (euroToTarget == null || euroToTarget == 0) {
            logger.warn("Currency {} not found in rates, defaulting rate to 1.0", targetCurrency);
            return BigDecimal.ONE;
        }

        BigDecimal euroToUsdRate = BigDecimal.valueOf(euroToUsd);
        BigDecimal euroToTargetRate = BigDecimal.valueOf(euroToTarget);

        // targetToUsdRate = euroToUsdRate / euroToTargetRate
        return euroToUsdRate.divide(euroToTargetRate, 6, RoundingMode.HALF_UP);
    }

    private BigDecimal convertCurrencyValueToUsd(String currency, BigDecimal amount) {
        BigDecimal rate = getCurrencyRate(currency);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public FixerioConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(FixerioConsumer consumer) {
        this.consumer = consumer;
    }
}
