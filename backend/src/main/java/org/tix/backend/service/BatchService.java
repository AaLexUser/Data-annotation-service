package org.tix.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tix.backend.model.Batch;
import org.tix.backend.model.Task;
import org.tix.backend.repository.BatchRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

@Service
public class BatchService {
    private final BatchRepository batchRepository;

    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public Object getAllBatches() {
        return null;
    }

    public Object loadBatch(Batch batch) {
        return null;
    }

    public Batch saveBatch(MultipartFile file, Integer overlaps) throws IOException {
        Batch batch = new Batch();
        batch.setName(file.getName());
        batch.setFormat(file.getContentType());
        batch.setIsActive(true);
        batch.setUploadedAt(LocalDateTime.now());
        parseBatchContentToRowDictionaryInTask(file);
        return batchRepository.save(batch);

    }

    public void parseBatchContentToRowDictionaryInTask(MultipartFile file) {
        Task task = new Task();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
