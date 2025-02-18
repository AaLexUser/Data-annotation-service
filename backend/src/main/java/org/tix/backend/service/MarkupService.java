package org.tix.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tix.backend.dto.MarkupDTO;
import org.tix.backend.model.Batch;
import org.tix.backend.model.Markup;
import org.tix.backend.repository.BatchRepository;
import org.tix.backend.repository.MarkupRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkupService {
    private final MarkupRepository markupRepository;
    private final BatchRepository batchRepository;

    @Transactional
    public Markup createMarkup(MarkupDTO dto) {
        Batch batch = batchRepository.findById(dto.getBatchId().longValue())
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        // Deactivate current active markup if exists
        markupRepository.findByBatchIdAndIsActiveTrue(batch)
                .ifPresent(markup -> {
                    markup.setActive(false);
                    markupRepository.save(markup);
                });

        // Create new markup version
        Integer nextVersion = markupRepository.findMaxVersionByBatchId(batch) + 1;
        
        Markup newMarkup = new Markup();
        newMarkup.setBatchId(batch);
        newMarkup.setVersion(nextVersion);
        newMarkup.setActive(true);
        newMarkup.setElements(dto.getElements());

        return markupRepository.save(newMarkup);
    }

    public List<Markup> getMarkupHistory(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        return markupRepository.findByBatchIdOrderByVersionDesc(batch);
    }

    public Markup getActiveMarkup(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        return markupRepository.findByBatchIdAndIsActiveTrue(batch)
                .orElseThrow(() -> new RuntimeException("No active markup found for batch"));
    }

    @Transactional
    public Markup activateVersion(Long batchId, Integer version) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        // Deactivate current active markup
        markupRepository.findByBatchIdAndIsActiveTrue(batch)
                .ifPresent(markup -> {
                    markup.setActive(false);
                    markupRepository.save(markup);
                });

        // Activate requested version
        Markup markupToActivate = markupRepository.findByBatchIdAndVersion(batch, version)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Markup version not found"));

        markupToActivate.setActive(true);
        return markupRepository.save(markupToActivate);
    }
}
