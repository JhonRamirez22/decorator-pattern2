package co.ceiba.transfers.infrastructure.adapter.out.memory;

import co.ceiba.transfers.domain.port.out.AuditLog;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryAuditLog implements AuditLog {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<String> entries = new ArrayList<>();

    @Override
    public void record(String entry) {
        entries.add(LocalTime.now().format(TIME) + " " + entry);
    }

    @Override
    public List<String> entries() {
        return Collections.unmodifiableList(entries);
    }
}
