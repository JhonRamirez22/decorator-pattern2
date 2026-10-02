package co.ceiba.transfers.domain.port.out;

import co.ceiba.transfers.domain.model.TransferTemplate;

import java.util.List;
import java.util.Optional;

public interface TransferTemplateRepository {
    List<TransferTemplate> findAll();
    Optional<TransferTemplate> findById(String id);
    void save(TransferTemplate template);
}
