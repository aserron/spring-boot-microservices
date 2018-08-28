/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.data.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import aserron.dlocal.demo.pm.rest.controllers.MerchantNotFoundException;

/**
 * Expose the Merchant REST service endpoints Allow to perform the "check"
 * request.
 *
 * @author Andres
 */
public class MerchantService {

    static private String BASE_URL = "http://localhost:8081/merchant/api";

    private RestTemplate restTemplate;    

    public MerchantService() {
    	
        RestTemplateBuilder builder = new RestTemplateBuilder();
        this.restTemplate = builder.build();
    }

    /**
     * Perform a GET request to the "check" endpoint at the merchant service.
     *
     * @param id
     * @return
     * @throws MerchantNotFoundException
     */
    public ResponseEntity<String> getMerchantById(Long id)
            throws MerchantNotFoundException {

        ResponseEntity<String> result;

        // REST Request url
        String url = BASE_URL + "/check/" + id.toString();

        /*
        HttpHeaders headers = new HttpHeaders();        
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET,
            entity, String.class);
         */
        result = this.restTemplate.getForEntity(url, String.class);

        // DEBUG
        System.out.println("MerchantService > Response RAW:" + result);

        return result;
    }

}
