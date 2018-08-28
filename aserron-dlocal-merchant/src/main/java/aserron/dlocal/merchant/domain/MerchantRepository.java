/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.merchant.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface MerchantRepository extends CrudRepository<Merchant, Long> {

    @Override
    public Collection<Merchant> findAll();
    
}
