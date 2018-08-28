/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.consumer.fixerio;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import aserron.dlocal.demo.pm.PaymentApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * FixerIO REST Consumer FixerService Helper encapsulates fixer.io REST method
 * requests and object serialization.
 *
 * Data bind uses FixerResponse DAO.
 *
 * @todo Switch from file json to REST json source.
 * @todo import maths from the other project's
 * @author Andres
 */
public class FixerioConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentApplication.class);

    /**
     * Fixer.io url call base pattern.
     * example:
     * http://data.fixer.io/api/latest?access_key=3e24b0b97ad902e99c7901eb1a9d879e&format=1
     */    
    public static final  String URL = "http://data.fixer.io/api/latest?access_key=%1$s";
    

    private String apiKey   = "3e24b0b97ad902e99c7901eb1a9d879e";

    private RestTemplate    restTemplate;
    private FixerioResponse response;


    public FixerioConsumer() {

        // create rest template
        RestTemplateBuilder builder = new RestTemplateBuilder();
        restTemplate = builder.build();

        // default empty response.
        response = new FixerioResponse();
    }

    public FixerioResponse getLatest() throws RestClientException {

        FixerioResponse res;

        res = restTemplate.getForObject(this.getFixerioUrl(), FixerioResponse.class);

        this.setResponse(res);
        
        if(PaymentApplication.DEBUG_SERVICE) {
            logFixerResponse(res);
        }
        
        return res;
    }    

    // debug mapped ratios
    private void logFixerResponse(FixerioResponse res){
        // logger.info("FixerIO Latest: {}", this);
        res.getRates().forEach((String t, Double u) -> {
            logger.info("> Rate {}:{}", t, u);
        });
    }

    // Implementation
    /**
     * Fixer.IO request builder Append Access Key stored in apiKey property.
     * Currently only latest endpoint is available
     *
     * @return A FixerIO endpoint url for the present apiKey.
     */
    private String getFixerioUrl() {
        return String.format(URL, this.getApiKey());
    }


    // Accessors 
    public FixerioResponse getResponse() {
        return this.response;
    }

    private void setResponse(FixerioResponse response) {
        this.response = response;
    }

    private String getApiKey() {
        return this.apiKey;
    }

    private void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }    

}
