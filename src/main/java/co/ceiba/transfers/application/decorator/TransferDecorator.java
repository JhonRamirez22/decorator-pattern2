package co.ceiba.transfers.application.decorator;

import co.ceiba.transfers.domain.model.TransferReceipt;
import co.ceiba.transfers.domain.model.TransferRequest;
import co.ceiba.transfers.domain.port.in.TransferMoneyUseCase;

/**
 * Base decorator: implements the same port it wraps and delegates to it by default.
 * Subclasses add behavior before and/or after calling {@link #proceed}.
 */
public abstract class TransferDecorator implements TransferMoneyUseCase {

    private final TransferMoneyUseCase wrapped;

    protected TransferDecorator(TransferMoneyUseCase wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public TransferReceipt transfer(TransferRequest request) {
        return proceed(request);
    }

    protected TransferReceipt proceed(TransferRequest request) {
        return wrapped.transfer(request);
    }
}
