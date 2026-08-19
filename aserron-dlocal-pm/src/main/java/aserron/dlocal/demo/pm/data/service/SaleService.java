package aserron.dlocal.demo.pm.data.service;

import aserron.dlocal.demo.pm.data.domain.Sale;
import aserron.dlocal.demo.pm.data.repositories.SaleRepository;
import aserron.dlocal.demo.pm.rest.dto.BalanceResponse;
import aserron.dlocal.demo.pm.rest.dto.CreateSaleRequest;
import java.util.Date;
import java.util.UUID;

public interface SaleService {

    Sale create(CreateSaleRequest request);

    Sale create(CreateSaleRequest request, String idempotencyKey);

    Sale getById(UUID id);

    BalanceResponse balanceByMerchantId(Long merchantId);

    BalanceResponse balance(Long merchantId, Date from, Date to);

    SaleRepository getSaleRepository();

    MerchantService getMerchantService();
}
