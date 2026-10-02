package co.ceiba.transfers.domain.model;

import java.math.BigDecimal;

public record TransferRequest(String sourceAccountId, String targetAccountId, BigDecimal amount) {

    public TransferRequest {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (sourceAccountId.equals(targetAccountId)) {
            throw new IllegalArgumentException("Source and target accounts must be different");
        }
    }
}
