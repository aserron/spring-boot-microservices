package aserron.dlocal.demo.pm;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import aserron.dlocal.demo.pm.consumer.fixerio.FixerioService;


@SpringBootApplication
public class PaymentApplication {
    
    public static final boolean DEBUG_SERVICE = false;
    
    private static final Logger  LOG   = LoggerFactory.getLogger(PaymentApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
    
    @SuppressWarnings("unused")
    private void testConvertionService() throws InterruptedException
    {
        FixerioService service = new FixerioService();
            
        try {
            BigDecimal res;
            BigDecimal bigValue;

            bigValue = BigDecimal.valueOf(100.00);
            res = service.convertCurrencyAmount("UYU",bigValue);
            LOG.info("CAMBIO 100 UYU to USD={}", res.toString());

            Thread.sleep(3500);
            bigValue = BigDecimal.valueOf(100.00);
            res = service.convertCurrencyAmount("EUR", bigValue);
            LOG.info("CAMBIO 100 EUR to USD={}", res.toString());

        } catch (RuntimeException e) {
            LOG.error(e.getMessage());
        }
    }
}
