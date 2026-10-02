package co.ceiba.transfers.application.service;

import co.ceiba.transfers.application.factory.InternalTransferFactory;
import co.ceiba.transfers.application.factory.InterbankTransferFactory;
import co.ceiba.transfers.application.factory.TransferProfileFactory;
import co.ceiba.transfers.domain.exception.AccountNotFoundException;
import co.ceiba.transfers.domain.model.Account;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.model.TransferTemplate;
import co.ceiba.transfers.domain.port.out.AccountRepository;
import co.ceiba.transfers.domain.port.out.TransferTemplateRepository;

import java.util.List;
import java.util.UUID;

/** Creates presets with factories and saves customized copies through Prototype. */
public final class TransferTemplateService {

    private final TransferTemplateRepository templates;
    private final AccountRepository accounts;

    public TransferTemplateService(TransferTemplateRepository templates, AccountRepository accounts) {
        this.templates = templates;
        this.accounts = accounts;
        if (templates.findAll().isEmpty()) {
            templates.save(new InternalTransferFactory().createTemplate());
            templates.save(new InterbankTransferFactory().createTemplate());
        }
    }

    public List<TransferTemplate> findAll() {
        return templates.findAll();
    }

    public TransferTemplate saveCopy(String prototypeId, String name,
                                    TransferRequest request, List<String> decorators) {
        if (name == null || name.isBlank() || name.strip().length() > 80) {
            throw new IllegalArgumentException("Template name must contain between 1 and 80 characters");
        }
        TransferTemplate prototype = templates.findById(prototypeId)
                .orElseThrow(() -> new IllegalArgumentException("Select an existing template"));
        Account source = accounts.findById(request.sourceAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.sourceAccountId()));
        Account target = accounts.findById(request.targetAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.targetAccountId()));
        TransferProfileFactory factory = source.isSameBank(target)
                ? new InternalTransferFactory() : new InterbankTransferFactory();
        TransferTemplate copy = prototype.copy(UUID.randomUUID().toString(), name.strip())
                .withConfiguration(request, decorators, factory.createPolicy().profile());
        templates.save(copy);
        return copy;
    }
}
