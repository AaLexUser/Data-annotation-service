package org.tix.backend.dto.mapper;

import org.springframework.stereotype.Component;
import org.tix.backend.dto.MarkupDTO;
import org.tix.backend.model.Markup;
import org.tix.backend.repository.BatchRepository;

@Component
public class MarkupMapper {
    private final BatchRepository batchRepository;

    public MarkupMapper(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public Markup toEntity(MarkupDTO dto) {
        Markup markup = new Markup();
        markup.setBatchId(batchRepository.findById((long) dto.getBatchId()).orElseThrow());
        markup.setElements(dto.getElements());
        return markup;
    }

    public MarkupDTO toDto(Markup markup) {
        MarkupDTO dto = new MarkupDTO();
        dto.setBatchId(markup.getBatchId().getId().intValue());
        dto.setElements(markup.getElements());
        return dto;
    }
}
