package org.tix.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.tix.backend.dto.EducationalBatchDTO;
import org.tix.backend.model.Batch;
import org.tix.backend.model.Task;
import org.tix.backend.model.UserMarkup;
import org.tix.backend.repository.BatchRepository;
import org.tix.backend.repository.TaskRepository;
import org.tix.backend.repository.UserMarkupRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class EducationalService {
    private final BatchRepository batchRepository;
    private final TaskRepository taskRepository;
    private final UserMarkupRepository userMarkupRepository;

    public EducationalService(BatchRepository batchRepository, 
                            TaskRepository taskRepository,
                            UserMarkupRepository userMarkupRepository) {
        this.batchRepository = batchRepository;
        this.taskRepository = taskRepository;
        this.userMarkupRepository = userMarkupRepository;
    }

    public void setEducationalBatch(EducationalBatchDTO dto) {
        Batch batch = batchRepository.findById(dto.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        
        batch.setIsEducational(true);
        batch.setCorrectAnswers(dto.getCorrectAnswers());
        
        batchRepository.save(batch);
    }

    public Map<String, Object> checkAnswers(Long taskId, Long assessorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getBatchId().getIsEducational()) {
            throw new RuntimeException("This is not an educational batch");
        }

        UserMarkup userMarkup = userMarkupRepository.findByTaskIdAndAssessorId(taskId, assessorId)
                .orElseThrow(() -> new RuntimeException("No markup found for this task and assessor"));

        String correctAnswer = task.getBatchId().getCorrectAnswers().get(taskId);
        if (correctAnswer == null) {
            throw new RuntimeException("No correct answer found for this task");
        }

        Map<String, String> correctSelections = parseJsonToMap(correctAnswer);
        Map<String, String> userSelections = userMarkup.getSelections();

        boolean isCorrect = compareSelections(correctSelections, userSelections);

        Map<String, Object> result = new HashMap<>();
        result.put("isCorrect", isCorrect);
        result.put("correctAnswer", correctSelections);
        
        return result;
    }

    private Map<String, String> parseJsonToMap(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON answer", e);
        }
    }

    private boolean compareSelections(Map<String, String> correct, Map<String, String> user) {
        // First check if all required selections are present
        for (Map.Entry<String, String> entry : correct.entrySet()) {
            String correctValue = entry.getValue();
            String userValue = user.get(entry.getKey());
            
            // Skip empty values in correct answer
            if (correctValue == null || correctValue.isEmpty()) {
                continue;
            }
            
            // If user doesn't have the key or value doesn't match
            if (userValue == null || !correctValue.equals(userValue)) {
                return false;
            }
        }
        
        // Check that user hasn't selected anything that should be unselected
        for (Map.Entry<String, String> entry : user.entrySet()) {
            String userValue = entry.getValue();
            String correctValue = correct.get(entry.getKey());
            
            // Skip empty values in user answer
            if (userValue == null || userValue.isEmpty()) {
                continue;
            }
            
            // If this key is not in correct answers or has different value
            if (correctValue == null || !userValue.equals(correctValue)) {
                return false;
            }
        }
        
        return true;
    }
} 