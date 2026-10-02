package co.ceiba.transfers.domain.model;

import co.ceiba.transfers.domain.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Account {

    private final String id;
    private final String holder;
    private final String bank;
    private final boolean gmfExempt;
    private final Set<String> knownRecipients = new HashSet<>();
    private BigDecimal balance;
    private BigDecimal transferredToday = BigDecimal.ZERO;

    public Account(String id, String holder, String bank, BigDecimal balance, boolean gmfExempt) {
        this.id = id;
        this.holder = holder;
        this.bank = bank;
        this.balance = balance;
        this.gmfExempt = gmfExempt;
    }

    public void debit(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(id, amount);
        }
        balance = balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public boolean hasFunds(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public boolean knowsRecipient(String accountId) {
        return knownRecipients.contains(accountId);
    }

    public void registerRecipient(String accountId) {
        knownRecipients.add(accountId);
    }

    public void registerDailyTransfer(BigDecimal amount) {
        transferredToday = transferredToday.add(amount);
    }

    public boolean isSameBank(Account other) {
        return bank.equals(other.bank);
    }

    public String getId() { return id; }
    public String getHolder() { return holder; }
    public String getBank() { return bank; }
    public BigDecimal getBalance() { return balance; }
    public BigDecimal getTransferredToday() { return transferredToday; }
    public boolean isGmfExempt() { return gmfExempt; }
}
