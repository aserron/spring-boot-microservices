package aserron.dlocal.demo.pm.data.service;

import aserron.dlocal.demo.pm.rest.controllers.MerchantNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class MerchantService {

    private static final Logger logger = LoggerFactory.getLogger(MerchantService.class);

    @Value("${merchant.service.url:http://localhost:8081/merchant/api}")
    private String baseUrl = "http://localhost:8081/merchant/api";

    private RestTemplate restTemplate;

    @Autowired
    public MerchantService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public MerchantService() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        this.restTemplate = builder.build();
    }

    public MerchantService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> getMerchantById(Long id) throws MerchantNotFoundException {
        if (id == null) {
            throw new MerchantNotFoundException("null");
        }

        String url = baseUrl + "/check/" + id;
        try {
            return this.restTemplate.getForEntity(url, String.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Merchant {} not found (HTTP 404 from Merchant service)", id);
                throw new MerchantNotFoundException(id.toString());
            }
            throw e;
        } catch (Exception e) {
            logger.warn("Error calling Merchant service at {}: {}", url, e.getMessage());
            throw new MerchantNotFoundException(id.toString());
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
