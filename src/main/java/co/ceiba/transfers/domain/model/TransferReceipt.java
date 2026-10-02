package co.ceiba.transfers.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransferReceipt {

    private final TransferRequest request;
    private final List<ReceiptLine> lines = new ArrayList<>();
    private TransferStatus status;
    private String message;

    private TransferReceipt(TransferRequest request, TransferStatus status, String message) {
        this.request = request;
        this.status = status;
        this.message = message;
    }

    public static TransferReceipt approved(TransferRequest request) {
        return new TransferReceipt(request, TransferStatus.APPROVED, "COMPLETED");
    }

    public static TransferReceipt stopped(TransferRequest request, TransferStatus status,
                                          String layer, String reasonCode) {
        TransferReceipt receipt = new TransferReceipt(request, status, reasonCode);
        receipt.addLine(new ReceiptLine(layer, reasonCode, BigDecimal.ZERO));
        return receipt;
    }

    public void addLine(ReceiptLine line) {
        lines.add(line);
    }

    public boolean isApproved() {
        return status == TransferStatus.APPROVED;
    }

    /** Total money that left the source account: amount plus every charge. */
    public BigDecimal totalDebited() {
        if (!isApproved()) {
            return BigDecimal.ZERO;
        }
        return lines.stream().map(ReceiptLine::charge).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TransferRequest getRequest() { return request; }
    public List<ReceiptLine> getLines() { return Collections.unmodifiableList(lines); }
    public TransferStatus getStatus() { return status; }
    public String getMessage() { return message; }
}
