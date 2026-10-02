package co.ceiba.transfers.application.factory;

import co.ceiba.transfers.domain.model.TransferPolicy;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.model.TransferRequestBuilder;
import co.ceiba.transfers.domain.model.TransferTemplate;

import java.math.BigDecimal;
import java.util.List;

/** Concrete family for transfers between accounts at the same bank. */
public final class InternalTransferFactory implements TransferProfileFactory {

    @Override
    public TransferPolicy createPolicy() {
        return new TransferPolicy("INTERNAL",
                List.of("AUDIT", "NOTIFICATION", "FRAUD", "DAILY_LIMIT", "GMF"));
    }

    @Override
    public TransferTemplate createTemplate() {
        TransferPolicy policy = createPolicy();
        TransferRequest request = new TransferRequestBuilder()
                .source("1001").target("1002").amount(new BigDecimal("500000")).build();
        return new TransferTemplate("internal-default", "Entre cuentas Ceiba", policy.profile(),
                request, policy.decorators(), true);
    }
}
