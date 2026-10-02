package co.ceiba.transfers.domain.port.out;

import java.util.List;

public interface AuditLog {

    void record(String entry);

    List<String> entries();
}
