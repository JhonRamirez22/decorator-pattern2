package co.ceiba.transfers.application.decorator;

import co.ceiba.transfers.domain.model.Account;
import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.model.TransferStatus;
import co.ceiba.transfers.domain.port.in.TransferMoneyUseCase;
import co.ceiba.transfers.domain.port.out.AccountRepository;

import java.math.BigDecimal;

/** Enforces the maximum amount a customer can move per day through the digital channel. */
public class DailyLimitDecorator extends TransferDecorator {

    public static final String LAYER = "DAILY_LIMIT";
    private static final BigDecimal DAILY_LIMIT = new BigDecimal("3000000");

    private final AccountRepository accountRepository;

    public DailyLimitDecorator(TransferMoneyUseCase wrapped, AccountRepository accountRepository) {
        super(wrapped);
        this.accountRepository = accountRepository;
    }

    @Override
    public TransferReceipt transfer(TransferRequest request) {
        Account source = accountRepository.findById(request.sourceAccountId()).orElse(null);
        if (source != null && source.getTransferredToday().add(request.amount()).compareTo(DAILY_LIMIT) > 0) {
            return TransferReceipt.stopped(request, TransferStatus.REJECTED, LAYER, "DAILY_LIMIT_EXCEEDED");
        }

        TransferReceipt receipt = proceed(request);
        if (receipt.isApproved() && source != null) {
            source.registerDailyTransfer(request.amount());
        }
        return receipt;
    }
}
