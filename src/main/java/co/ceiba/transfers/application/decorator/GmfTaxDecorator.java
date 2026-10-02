package co.ceiba.transfers.application.decorator;

import co.ceiba.transfers.domain.model.Account;
import co.ceiba.transfers.domain.model.ReceiptLine;
import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.port.in.TransferMoneyUseCase;
import co.ceiba.transfers.domain.port.out.AccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Colombian financial transactions tax (GMF, "4x1000"). It is charged on everything
 * debited so far, so if this decorator wraps the ACH fee, the fee is taxed too.
 */
public class GmfTaxDecorator extends TransferDecorator {

    public static final String LAYER = "GMF";
    private static final BigDecimal RATE = new BigDecimal("0.004");

    private final AccountRepository accountRepository;

    public GmfTaxDecorator(TransferMoneyUseCase wrapped, AccountRepository accountRepository) {
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
        if (source.isGmfExempt()) {
            receipt.addLine(new ReceiptLine(LAYER, "GMF_EXEMPT_ACCOUNT", BigDecimal.ZERO));
            return receipt;
        }

        BigDecimal tax = receipt.totalDebited().multiply(RATE).setScale(0, RoundingMode.HALF_UP);
        source.debit(tax);
        receipt.addLine(new ReceiptLine(LAYER, "GMF_4X1000", tax));
        return receipt;
    }
}
