/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aserron.dlocal.demo.pm.data.service;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.repositories.SaleRepository;
import aserron.dlocal.demo.pm.rest.dto.BalanceResponse;
import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import java.util.UUID;

/**
 * Sale Service Interface 
 * 
 * The service expose functionality for dealing with
 * Sale entity creation, Rest checking for 
 */
public interface SaleService {

    public Sale create(CreateSaleRequest request);

    public Sale getById(UUID id);

    public BalanceResponse balanceByMerchantId(Long merchantId);

    public SaleRepository  getSaleRepository();

    public MerchantService getMerchantService();
    
}
