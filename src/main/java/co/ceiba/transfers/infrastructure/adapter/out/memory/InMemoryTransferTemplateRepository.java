package co.ceiba.transfers.infrastructure.adapter.out.memory;

import co.ceiba.transfers.domain.model.TransferTemplate;
import co.ceiba.transfers.domain.port.out.TransferTemplateRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryTransferTemplateRepository implements TransferTemplateRepository {

    private final Map<String, TransferTemplate> templates = new LinkedHashMap<>();

    @Override
    public synchronized List<TransferTemplate> findAll() {
        return List.copyOf(templates.values());
    }

    @Override
    public synchronized Optional<TransferTemplate> findById(String id) {
        return Optional.ofNullable(templates.get(id));
    }

    @Override
    public synchronized void save(TransferTemplate template) {
        templates.put(template.getId(), template);
    }
}
