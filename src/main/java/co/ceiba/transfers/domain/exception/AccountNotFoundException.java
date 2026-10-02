package co.ceiba.transfers.domain.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountId) {
        super("Account " + accountId + " does not exist");
    }
}
