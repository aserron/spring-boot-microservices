package aserron.dlocal.merchant.rest;

import aserron.dlocal.merchant.MerchantRestApplication;
import aserron.dlocal.merchant.domain.Merchant;
import aserron.dlocal.merchant.domain.MerchantRepository;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Ignore
public class MerchantControllerTest {

    // json media type
    private MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    // @Test
    public final void testGetMerchantById() {
        fail("Not yet implemented"); // TODO
    }

    // inject and inspect the converters.
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    // testing beans    
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    

    @Autowired
    private MerchantRepository merchantRepository;

    @BeforeClass
    public static void  beforeSetUp() {}

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        this.merchantRepository.deleteAll();
        
        Merchant m = new Merchant((long) 1, "Markus Ink.");
        this.merchantRepository.save(m);
        this.merchantRepository.save(new Merchant((long) 2, "Korpo corp."));
    }

    @Test
    public void merchantNotFound() throws Exception {

        
        String notPresentIdStr = "3";
        Long   notPresentId    = Long.valueOf(notPresentIdStr);
        
        Merchant notPresentMerchant = new Merchant(notPresentId, "Oran");

        mockMvc.perform( get("/merchant/check/" + notPresentIdStr)
                            .content(this.json(notPresentMerchant))
                            .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);

        return mockHttpOutputMessage.getBodyAsString();
    }

}
