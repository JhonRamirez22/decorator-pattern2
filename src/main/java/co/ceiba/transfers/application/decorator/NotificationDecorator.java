package co.ceiba.transfers.application.decorator;

import co.ceiba.transfers.domain.model.ReceiptLine;
import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.port.in.TransferMoneyUseCase;
import co.ceiba.transfers.domain.port.out.NotificationSender;

import java.math.BigDecimal;

/** Sends the customer an SMS with the final outcome of the transfer. */
public class NotificationDecorator extends TransferDecorator {

    public static final String LAYER = "NOTIFICATION";

    private final NotificationSender notificationSender;

    public NotificationDecorator(TransferMoneyUseCase wrapped, NotificationSender notificationSender) {
        super(wrapped);
        this.notificationSender = notificationSender;
    }

    @Override
    public TransferReceipt transfer(TransferRequest request) {
        TransferReceipt receipt = proceed(request);

        notificationSender.send(request.sourceAccountId(), "Transfer to " + request.targetAccountId()
                + " " + receipt.getStatus() + ", total debited " + receipt.totalDebited().toPlainString());
        receipt.addLine(new ReceiptLine(LAYER, "SMS_SENT", BigDecimal.ZERO));
        return receipt;
    }
}
