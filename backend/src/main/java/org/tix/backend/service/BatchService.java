package org.tix.backend.service;

import com.opencsv.CSVReader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tix.backend.dto.stat.TaskStatForAdmin;
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

    public List<TaskStatForAdmin> getAllTaskByBatchId(Long batchId) {

        List<TaskStatForAdmin> taskStatForAdminList = new ArrayList<>();
        List<Task> taskList = taskRepository.findAllByBatchId(batchRepository.findById(batchId).orElseThrow());
        for (Task task : taskList) {
            TaskStatForAdmin taskStatForAdmin = new TaskStatForAdmin();
            taskStatForAdmin.setId(task.getId());
            taskStatForAdmin.setStatus(defineStatusTask(task));
            taskStatForAdmin.setFinalMark(defineFinalMark(task));
            taskStatForAdminList.add(taskStatForAdmin);
        }
        return taskStatForAdminList;
    }

    private String defineStatusTask(Task task) {
        int tmp = task.getCurrentOverlaps();
        if (tmp == 0){
            return "AWAITING";
        } else if (tmp == task.getFiniteOverlaps()) {
            return "FINITE";
        }
        else{
            return "PARTIALLY";
        }
    }

    private String defineFinalMark(Task task){
        String finalMark = task.getFinalMarkup();
        if (finalMark == null || finalMark.isEmpty()) {
            return "N/A";
        }

        finalMark = finalMark.replaceAll("[{}]", ""); // Убираем фигурные скобки
        String[] pairs = finalMark.split(", ");

        StringBuilder formattedResult = new StringBuilder();
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2 && !keyValue[1].trim().isEmpty()) { // Пропускаем пустые значения
                formattedResult.append(keyValue[0].trim()).append(": ").append(keyValue[1].trim()).append(" | ");
            }
        }

        return !formattedResult.isEmpty()
                ? formattedResult.substring(0, formattedResult.length() - 3) // Убираем лишний `|`
                : "N/A";
    }

    public Batch updateName(Long batchId, String name) {
        Batch batch = batchRepository.findById(batchId).orElseThrow(() -> new RuntimeException("Batch not found"));
        batch.setName(name);
        return batchRepository.save(batch);
    }
}
