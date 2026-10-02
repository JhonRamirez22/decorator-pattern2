package co.ceiba.transfers.application.factory;

import co.ceiba.transfers.domain.model.TransferPolicy;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.model.TransferRequestBuilder;
import co.ceiba.transfers.domain.model.TransferTemplate;

import java.math.BigDecimal;
import java.util.List;

/** Concrete family for transfers between different banks. */
public final class InterbankTransferFactory implements TransferProfileFactory {

    @Override
    public TransferPolicy createPolicy() {
        return new TransferPolicy("INTERBANK",
                List.of("AUDIT", "NOTIFICATION", "FRAUD", "DAILY_LIMIT", "GMF", "ACH_FEE"));
    }

    @Override
    public TransferTemplate createTemplate() {
        TransferPolicy policy = createPolicy();
        TransferRequest request = new TransferRequestBuilder()
                .source("1002").target("2001").amount(new BigDecimal("500000")).build();
        return new TransferTemplate("interbank-default", "Transferencia interbancaria",
                policy.profile(), request, policy.decorators(), true);
    }
}
