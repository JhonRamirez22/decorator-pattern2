package co.ceiba.transfers.application.decorator;

import co.ceiba.transfers.domain.model.Account;
import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.model.TransferStatus;
import co.ceiba.transfers.domain.port.in.TransferMoneyUseCase;
import co.ceiba.transfers.domain.port.out.AccountRepository;

import java.math.BigDecimal;

/**
 * Holds high-value transfers to recipients the customer has never paid before,
 * the typical rule banks apply to stop account-takeover fraud.
 */
public class FraudScreeningDecorator extends TransferDecorator {

    public static final String LAYER = "FRAUD";
    private static final BigDecimal THRESHOLD = new BigDecimal("2000000");

    private final AccountRepository accountRepository;

    public FraudScreeningDecorator(TransferMoneyUseCase wrapped, AccountRepository accountRepository) {
        super(wrapped);
        this.accountRepository = accountRepository;
    }

    @Override
    public TransferReceipt transfer(TransferRequest request) {
        Account source = accountRepository.findById(request.sourceAccountId()).orElse(null);
        boolean newRecipient = source != null && !source.knowsRecipient(request.targetAccountId());

        if (newRecipient && request.amount().compareTo(THRESHOLD) > 0) {
            return TransferReceipt.stopped(request, TransferStatus.ON_HOLD, LAYER, "NEW_RECIPIENT_HIGH_AMOUNT");
        }

        TransferReceipt receipt = proceed(request);
        if (receipt.isApproved() && source != null) {
            source.registerRecipient(request.targetAccountId());
        }
        return receipt;
    }
}
