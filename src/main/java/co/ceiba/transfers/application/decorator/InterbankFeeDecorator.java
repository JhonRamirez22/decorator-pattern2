package co.ceiba.transfers.application.decorator;

import co.ceiba.transfers.domain.model.Account;
import co.ceiba.transfers.domain.model.ReceiptLine;
import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.port.in.TransferMoneyUseCase;
import co.ceiba.transfers.domain.port.out.AccountRepository;

import java.math.BigDecimal;

/** Charges the ACH fee when the money goes to a different bank. */
public class InterbankFeeDecorator extends TransferDecorator {

    public static final String LAYER = "ACH_FEE";
    private static final BigDecimal ACH_FEE = new BigDecimal("7900");

    private final AccountRepository accountRepository;

    public InterbankFeeDecorator(TransferMoneyUseCase wrapped, AccountRepository accountRepository) {
        super(wrapped);
        this.accountRepository = accountRepository;
    }

    @Override
    public TransferReceipt transfer(TransferRequest request) {
        TransferReceipt receipt = proceed(request);
        if (!receipt.isApproved()) {
            return receipt;
        }

        Account source = accountRepository.findById(request.sourceAccountId()).orElseThrow();
        Account target = accountRepository.findById(request.targetAccountId()).orElseThrow();
        if (!source.isSameBank(target)) {
            source.debit(ACH_FEE);
            receipt.addLine(new ReceiptLine(LAYER, "ACH_INTERBANK_FEE", ACH_FEE));
        }
        return receipt;
    }
}
