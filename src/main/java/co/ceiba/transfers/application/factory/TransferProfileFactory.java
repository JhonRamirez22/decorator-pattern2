package co.ceiba.transfers.application.factory;

import co.ceiba.transfers.domain.model.TransferPolicy;
import co.ceiba.transfers.domain.model.TransferTemplate;

/** Abstract Factory for related products: a template and its decorator policy. */
public interface TransferProfileFactory {
    TransferPolicy createPolicy();
    TransferTemplate createTemplate();
}
