package co.ceiba.transfers.application.decorator;

import co.ceiba.transfers.domain.model.ReceiptLine;
import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.port.in.TransferMoneyUseCase;
import co.ceiba.transfers.domain.port.out.AuditLog;

import java.math.BigDecimal;

/**
 * Records every attempt and its outcome. Only what happens inside this layer is
 * audited: a transfer stopped by an outer decorator never reaches the log.
 */
public class AuditTrailDecorator extends TransferDecorator {

    public static final String LAYER = "AUDIT";

    private final AuditLog auditLog;

    public AuditTrailDecorator(TransferMoneyUseCase wrapped, AuditLog auditLog) {
        super(wrapped);
        this.auditLog = auditLog;
    }

    @Override
    public TransferReceipt transfer(TransferRequest request) {
        auditLog.record("ATTEMPT " + request.sourceAccountId() + " -> " + request.targetAccountId()
                + " amount=" + request.amount().toPlainString());

        TransferReceipt receipt = proceed(request);

        auditLog.record("RESULT " + receipt.getStatus() + " " + receipt.getMessage()
                + " debited=" + receipt.totalDebited().toPlainString());
        receipt.addLine(new ReceiptLine(LAYER, "AUDIT_RECORDED", BigDecimal.ZERO));
        return receipt;
    }
}
