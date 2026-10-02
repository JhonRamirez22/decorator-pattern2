package co.ceiba.transfers.domain.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountId, BigDecimal amount) {
        super("Account " + accountId + " has insufficient funds to debit " + amount.toPlainString());
    }
}
