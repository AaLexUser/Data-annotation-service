package org.tix.backend.service;

import org.springframework.stereotype.Service;
import org.tix.backend.dto.MarkupDTO;
import org.tix.backend.dto.mapper.MarkupMapper;
import org.tix.backend.model.Markup;
import org.tix.backend.repository.BatchRepository;
import org.tix.backend.repository.MarkupRepository;

@Service
public class MarkupService {
    private final MarkupRepository markupRepository;
    private final BatchRepository batchRepository;
    private final MarkupMapper markupMapper;

    public MarkupService(MarkupRepository markupRepository, BatchRepository batchRepository, MarkupMapper markupMapper) {
        this.markupRepository = markupRepository;
        this.batchRepository = batchRepository;
        this.markupMapper = markupMapper;
    }


    public Markup loadMarkup(MarkupDTO markupDTO) {
        Markup markup = markupMapper.toEntity(markupDTO);
        return markupRepository.save(markup);
    }

    public Markup getMarkupByBatchId(Long id) {
        return markupRepository.findByBatchId(batchRepository.findById(id).orElseThrow()).orElseThrow();
    }
}
