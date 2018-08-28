package aserron.dlocal.demo.pm.data.repositories;

import aserron.dlocal.demo.pm.data.domain.Merchant;
import org.springframework.data.repository.CrudRepository;

public interface MerchantRepository extends CrudRepository<Merchant, Long> {
}
