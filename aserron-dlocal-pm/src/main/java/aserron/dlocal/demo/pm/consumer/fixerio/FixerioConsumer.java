package aserron.dlocal.demo.pm.consumer.fixerio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FixerioConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FixerioConsumer.class);

    public static final String URL = "http://data.fixer.io/api/latest?access_key=%1$s";

    private String apiKey = "3e24b0b97ad902e99c7901eb1a9d879e";
    private RestTemplate restTemplate;
    private FixerioResponse response;

    public FixerioConsumer() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        this.restTemplate = builder.build();
        this.response = new FixerioResponse();
    }

    public FixerioConsumer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.response = new FixerioResponse();
    }

    public FixerioResponse getLatest() {
        try {
            FixerioResponse res = restTemplate.getForObject(this.getFixerioUrl(), FixerioResponse.class);
            if (res != null && res.getRates() != null && !res.getRates().isEmpty()) {
                this.response = res;
                return res;
            }
        } catch (Exception e) {
            logger.warn("Fixer.io remote call failed ({}), using fallback/cached exchange rates", e.getMessage());
        }

        if (this.response == null || this.response.getRates() == null || this.response.getRates().isEmpty()) {
            this.response = new FixerioResponse();
        }
        return this.response;
    }

    private String getFixerioUrl() {
        return String.format(URL, this.getApiKey());
    }

    public FixerioResponse getResponse() {
        return this.response;
    }

    public void setResponse(FixerioResponse response) {
        this.response = response;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
