package co.ceiba.transfers.domain.port.in;

import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;

/**
 * Component of the Decorator pattern. Both the basic service and every
 * decorator implement this port, so they can wrap each other freely.
 */
public interface TransferMoneyUseCase {

    TransferReceipt transfer(TransferRequest request);
}
