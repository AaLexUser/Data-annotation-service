package org.tix.backend.service;

import com.opencsv.CSVReader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tix.backend.model.Batch;
import org.tix.backend.model.Task;
import org.tix.backend.repository.BatchRepository;
import org.tix.backend.repository.TaskRepository;
import org.tix.backend.repository.UserRepository;
import org.tix.backend.util.Role;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BatchService {
    private final BatchRepository batchRepository;
    private final TaskRepository taskRepository;
    private static final int INIT_COUNT_OF_OVERLAPS = 0;
    private final UserRepository userRepository;

    public BatchService(BatchRepository batchRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Batch saveBatch(MultipartFile file, Integer overlaps, String batchName) throws IOException {
        Batch batch = new Batch();
        batch.setName(batchName);
        batch.setFormat(file.getContentType());
        batch.setIsActive(true);
        batch.setUploadedAt(LocalDateTime.now());
        batch = batchRepository.save(batch);
        parseBatchContentToRowDictionaryInTask(file, batch, overlaps);
        return batchRepository.save(batch);

    }

    public void parseBatchContentToRowDictionaryInTask(MultipartFile file, Batch batch, Integer overlaps) {
        List<Task> taskList = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                Task task = new Task();
                HashMap<String,String> map = new HashMap<>();
                for (int i = 0; i < line.length; i++) {
                    map.put(header[i], line[i]);
                }
                task.setRowFromBatch(map);
                task.setFiniteOverlaps(overlaps);
                task.setCurrentOverlaps(INIT_COUNT_OF_OVERLAPS);
                task.setBatchId(batch);
                taskList.add(task);
            }
            taskRepository.saveAll(taskList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Batch> getAllBatchesForAssessor(Long userId) {
        if (userRepository.findById(userId).orElseThrow().getRole() == Role.ADMIN) {
            return batchRepository.findAll();
        }
       return batchRepository.findAllByUserIdInAvailableUsers(userId);
    }
    public String getBatchStatus(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        return batch.getIsActive() ? "active" : "inactive";
    }

    public String toggleBatchStatus(Long batchId, String newStatus) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (!newStatus.equals("active") && !newStatus.equals("inactive")) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }

        batch.setIsActive(newStatus.equals("active"));
        batchRepository.save(batch);

        return batch.getIsActive() ? "active" : "inactive";
    }

    public ResponseEntity<?> getCurrentStatus(Long batchId) {
        return ResponseEntity.ok(Map.of("status", batchRepository.findById(batchId).orElseThrow().getIsActive() ? "active" : "inactive"));
    }
}
