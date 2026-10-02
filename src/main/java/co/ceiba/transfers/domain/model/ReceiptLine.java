package co.ceiba.transfers.domain.model;

import java.math.BigDecimal;

/**
 * One line of the receipt. Each decorator appends its own line, so the receipt
 * shows exactly which layers took part in the transfer.
 */
public record ReceiptLine(String layer, String concept, BigDecimal charge) {
}
