package co.ceiba.transfers.infrastructure.adapter.out.memory;

import co.ceiba.transfers.domain.model.Account;
import co.ceiba.transfers.domain.port.out.AccountRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accounts = new LinkedHashMap<>();

    public InMemoryAccountRepository() {
        Account laura = new Account("1001", "Laura Gómez", "Banco Ceiba", new BigDecimal("8500000"), true);
        Account andres = new Account("1002", "Andrés Rojas", "Banco Ceiba", new BigDecimal("2300000"), false);
        Account andina = new Account("2001", "Comercial Andina S.A.S.", "Banco Cordillera", new BigDecimal("15000000"), false);
        Account marta = new Account("2002", "Marta Ruiz", "Banco Cordillera", new BigDecimal("640000"), false);
        laura.registerRecipient("1002");
        andres.registerRecipient("1001");

        for (Account account : List.of(laura, andres, andina, marta)) {
            accounts.put(account.getId(), account);
        }
    }

    @Override
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }
}
