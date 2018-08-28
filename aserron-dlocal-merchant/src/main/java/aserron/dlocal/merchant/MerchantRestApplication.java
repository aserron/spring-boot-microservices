package aserron.dlocal.merchant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MerchantRestApplication {

    public static final boolean DEBUG_MODE = true;
    
    private static final Logger logger = LogManager.getLogger(MerchantRestApplication.class);
    
    
    
    public static void main(String[] args) {
        SpringApplication.run(MerchantRestApplication.class, args);
    }

}
