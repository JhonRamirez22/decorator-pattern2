package co.ceiba.transfers.domain.port.out;

import co.ceiba.transfers.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Optional<Account> findById(String id);

    List<Account> findAll();
}
