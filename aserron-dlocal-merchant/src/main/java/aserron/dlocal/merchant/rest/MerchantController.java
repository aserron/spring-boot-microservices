package aserron.dlocal.merchant.rest;

import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import aserron.dlocal.merchant.domain.Merchant;
import aserron.dlocal.merchant.domain.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.annotation.Validated;


@RestController
@Validated
public class MerchantController {

    public static final String BASE_URI = "merchant/api/";
    
    private MerchantRepository merchantRepository;    
    
    @Autowired
    public MerchantController(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @GetMapping(path = BASE_URI+"/all")
    public Collection<Merchant> getAllMerchants() {
        // This returns a JSON or XML with the users
        return merchantRepository.findAll();
    }

    @GetMapping(path = BASE_URI+"/check/{id}")
    Merchant getMerchantById(@PathVariable Long id) 
                                    throws MerchantNotFoundException
    {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new MerchantNotFoundException(id));     
    }    
    
}
