package co.ceiba.transfers.domain.model;

import java.util.List;

/** One product in a transfer profile family: its default decorator policy. */
public record TransferPolicy(String profile, List<String> decorators) {

    public TransferPolicy {
        decorators = List.copyOf(decorators);
    }
}
