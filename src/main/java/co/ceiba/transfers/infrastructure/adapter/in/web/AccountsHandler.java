package co.ceiba.transfers.infrastructure.adapter.in.web;

import co.ceiba.transfers.domain.model.Account;
import co.ceiba.transfers.domain.port.out.AccountRepository;
import com.sun.net.httpserver.HttpExchange;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** GET /api/accounts */
public class AccountsHandler extends JsonHandler {

    private final AccountRepository accountRepository;

    public AccountsHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    protected Object handleRequest(HttpExchange exchange) {
        List<Map<String, Object>> accounts = accountRepository.findAll().stream().map(this::toJson).toList();
        return accounts;
    }

    private Map<String, Object> toJson(Account account) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("id", account.getId());
        json.put("holder", account.getHolder());
        json.put("bank", account.getBank());
        json.put("balance", account.getBalance());
        json.put("transferredToday", account.getTransferredToday());
        json.put("gmfExempt", account.isGmfExempt());
        return json;
    }
}
