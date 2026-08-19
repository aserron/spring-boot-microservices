package aserron.dlocal.merchant.rest;

import aserron.dlocal.merchant.domain.Merchant;
import aserron.dlocal.merchant.domain.MerchantRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchantRepository merchantRepository;

    private Long merchantId1;
    private Long merchantId2;

    @Before
    public void setup() {
        merchantRepository.deleteAll();

        Merchant m1 = merchantRepository.save(new Merchant(null, "Markus Ink."));
        Merchant m2 = merchantRepository.save(new Merchant(null, "Korpo corp."));
        merchantId1 = m1.getId();
        merchantId2 = m2.getId();
    }

    @Test
    public void checkMerchantFound() throws Exception {
        mockMvc.perform(get("/merchant/api/check/" + merchantId1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(merchantId1.intValue())))
                .andExpect(jsonPath("$.name", is("Markus Ink.")));
    }

    @Test
    public void checkMerchantNotFound() throws Exception {
        mockMvc.perform(get("/merchant/api/check/999999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllMerchants() throws Exception {
        mockMvc.perform(get("/merchant/api/all")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
