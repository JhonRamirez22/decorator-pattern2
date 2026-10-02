package co.ceiba.transfers.application.service;

import co.ceiba.transfers.domain.exception.AccountNotFoundException;
import co.ceiba.transfers.domain.model.Account;
import co.ceiba.transfers.domain.model.ReceiptLine;
import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.port.in.TransferMoneyUseCase;
import co.ceiba.transfers.domain.port.out.AccountRepository;

/**
 * Concrete component: the bare money movement with no taxes, fees or controls.
 */
public class BasicTransferService implements TransferMoneyUseCase {

    private final AccountRepository accountRepository;

    public BasicTransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public TransferReceipt transfer(TransferRequest request) {
        Account source = findAccount(request.sourceAccountId());
        Account target = findAccount(request.targetAccountId());

        source.debit(request.amount());
        target.credit(request.amount());

        TransferReceipt receipt = TransferReceipt.approved(request);
        receipt.addLine(new ReceiptLine("BASE", "TRANSFERRED_AMOUNT", request.amount()));
        return receipt;
    }

    private Account findAccount(String id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }
}
