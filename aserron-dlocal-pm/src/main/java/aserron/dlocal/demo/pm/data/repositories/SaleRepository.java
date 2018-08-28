/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.data.repositories;

import aserron.dlocal.demo.pm.data.domain.Sale;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface SaleRepository extends CrudRepository<Sale, UUID> {  

    @Override
    public Collection<Sale> findAll();
    
    public Collection<Sale> findAllByMerchantId(Long merchantId);

    public Optional<Sale> findByMerchantIdAndTransactionId(Long merchantId, Long transactionId);

    public boolean existsByMerchantIdAndTransactionId(Long merchantId, Long transactionId);
    
}
