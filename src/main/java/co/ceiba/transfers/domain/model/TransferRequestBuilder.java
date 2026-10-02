package co.ceiba.transfers.domain.model;

import java.math.BigDecimal;

/** Builder that assembles a validated transfer request step by step. */
public final class TransferRequestBuilder {

    private String sourceAccountId;
    private String targetAccountId;
    private BigDecimal amount;

    public TransferRequestBuilder source(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
        return this;
    }

    public TransferRequestBuilder target(String targetAccountId) {
        this.targetAccountId = targetAccountId;
        return this;
    }

    public TransferRequestBuilder amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public TransferRequest build() {
        return new TransferRequest(sourceAccountId, targetAccountId, amount);
    }
}
