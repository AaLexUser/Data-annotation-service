package org.tix.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tix.backend.dto.HoneypotTaskDTO;
import org.tix.backend.model.Task;
import org.tix.backend.model.TaskAnswer;
import org.tix.backend.model.User;
import org.tix.backend.repository.TaskAnswerRepository;
import org.tix.backend.repository.TaskRepository;
import org.tix.backend.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HoneypotService {
    private final TaskAnswerRepository taskAnswerRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskAnswer createHoneypotTask(HoneypotTaskDTO dto) {
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        TaskAnswer honeypotAnswer = new TaskAnswer();
        honeypotAnswer.setTask(task);
        honeypotAnswer.setHoneypot(true);
        honeypotAnswer.setCorrectAnswers(dto.getCorrectAnswers());
        
        return taskAnswerRepository.save(honeypotAnswer);
    }

    public List<TaskAnswer> getHoneypotTasksByBatch(Long batchId) {
        List<Task> batchTasks = taskRepository.findByBatchId(batchId);
        return taskAnswerRepository.findByTaskInAndIsHoneypotTrue(batchTasks);
    }

    public Map<String, Object> checkAssessorAnswers(Long batchId, Long assessorId) {
        List<Task> batchTasks = taskRepository.findByBatchId(batchId);
        User assessor = userRepository.findById(assessorId)
                .orElseThrow(() -> new RuntimeException("Assessor not found"));

        List<TaskAnswer> honeypotAnswers = taskAnswerRepository.findByTaskInAndIsHoneypotTrue(batchTasks);
        List<TaskAnswer> assessorAnswers = taskAnswerRepository.findByTaskInAndAssessor(batchTasks, assessor);

        Map<String, Object> results = new HashMap<>();
        int totalTasks = honeypotAnswers.size();
        int correctTasks = 0;

        Map<Long, Map<String, Object>> taskResults = new HashMap<>();

        for (TaskAnswer honeypot : honeypotAnswers) {
            TaskAnswer assessorAnswer = assessorAnswers.stream()
                    .filter(a -> a.getTask().getId().equals(honeypot.getTask().getId()))
                    .findFirst()
                    .orElse(null);

            if (assessorAnswer != null) {
                Map<String, Object> taskResult = new HashMap<>();
                boolean isTaskCorrect = true;
                Map<String, Boolean> fieldResults = new HashMap<>();

                for (Map.Entry<String, String> entry : honeypot.getCorrectAnswers().entrySet()) {
                    String field = entry.getKey();
                    String correctAnswer = entry.getValue();
                    String assessorAnswerValue = assessorAnswer.getAnswers().get(field);

                    boolean isFieldCorrect = correctAnswer.equals(assessorAnswerValue);
                    fieldResults.put(field, isFieldCorrect);
                    if (!isFieldCorrect) {
                        isTaskCorrect = false;
                    }
                }

                if (isTaskCorrect) {
                    correctTasks++;
                }

                taskResult.put("isCorrect", isTaskCorrect);
                taskResult.put("fieldResults", fieldResults);
                taskResults.put(honeypot.getTask().getId(), taskResult);
            }
        }

        results.put("totalTasks", totalTasks);
        results.put("correctTasks", correctTasks);
        results.put("accuracy", totalTasks > 0 ? (double) correctTasks / totalTasks : 0);
        results.put("taskResults", taskResults);

        return results;
    }
} 