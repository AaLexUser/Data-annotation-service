package org.tix.backend.service;

import org.springframework.stereotype.Service;
import org.tix.backend.dto.stat.AdminPageStatisticDTO;
import org.tix.backend.dto.stat.AssessorStatisticDTO;
import org.tix.backend.dto.stat.AssessorEducationalStatDTO;
import org.tix.backend.dto.stat.EducationalStatisticDTO;
import org.tix.backend.model.Batch;
import org.tix.backend.model.Task;
import org.tix.backend.model.User;
import org.tix.backend.model.UserMarkup;
import org.tix.backend.repository.BatchRepository;
import org.tix.backend.repository.TaskRepository;
import org.tix.backend.repository.UserMarkupRepository;
import org.tix.backend.repository.UserRepository;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StatisticService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final BatchRepository batchRepository;
    private final UserMarkupRepository userMarkupRepository;

    public StatisticService(TaskRepository taskRepository, UserRepository userRepository, BatchRepository batchRepository, UserMarkupRepository userMarkupRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.batchRepository = batchRepository;
        this.userMarkupRepository = userMarkupRepository;
    }

    public AdminPageStatisticDTO getAdminPageStatistic(Long batchId) {
        AdminPageStatisticDTO adminPageStatisticDTO = new AdminPageStatisticDTO();
        Batch batch = batchRepository.findById(batchId).orElseThrow();
        List<Task> taskList = taskRepository.getAllByBatchId(batch);
        adminPageStatisticDTO.setCountOfTasks(String.valueOf(taskList.size()));

        int taskByOverlaps = taskList.get(0).getFiniteOverlaps() * taskList.size();
        int completedTask = taskList.stream().mapToInt(Task::getCurrentOverlaps).sum();

        Double percentOfCompletedTasks = Math.min(100.0, (completedTask * 100.0 / taskByOverlaps));
        if (percentOfCompletedTasks == 100.0) {
            batch.setIsActive(false);
            batchRepository.save(batch);
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        adminPageStatisticDTO.setPercentOfCompletedTasks(decimalFormat.format(percentOfCompletedTasks));
        return adminPageStatisticDTO;
    }

    public AssessorStatisticDTO getAssessorStatistic(Long userId){
        AssessorStatisticDTO assessorStatisticDTO = new AssessorStatisticDTO();
        User user = userRepository.findById(userId).orElseThrow();
        return assessorStatisticDTO;
    }

    public EducationalStatisticDTO getEducationalStatistics(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (!batch.getIsEducational()) {
            throw new RuntimeException("This is not an educational batch");
        }

        List<Task> tasks = taskRepository.getAllByBatchId(batch);
        EducationalStatisticDTO stats = new EducationalStatisticDTO();
        stats.setBatchId(batchId);
        stats.setBatchName(batch.getName());

        // Get all markups for this batch's tasks
        Set<Long> uniqueAssessors = new HashSet<>();
        int totalAttempts = 0;
        int correctAttempts = 0;

        for (Task task : tasks) {
            List<UserMarkup> markups = userMarkupRepository.findByTaskId(task.getId());
            
            for (UserMarkup markup : markups) {
                totalAttempts++;
                uniqueAssessors.add(markup.getAssessor().getId());

                // Get correct answer for this task
                String correctAnswerJson = batch.getCorrectAnswers().get(task.getId());
                if (correctAnswerJson != null) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, String> correctAnswer = mapper.readValue(correctAnswerJson, new TypeReference<Map<String, String>>() {});
                        
                        // Compare with user's answer
                        if (compareSelections(correctAnswer, markup.getSelections())) {
                            correctAttempts++;
                        }
                    } catch (Exception e) {
                        // Log error but continue processing
                        System.err.println("Error parsing correct answer for task " + task.getId() + ": " + e.getMessage());
                    }
                }
            }
        }

        stats.setTotalAttempts(totalAttempts);
        stats.setCorrectAttempts(correctAttempts);
        stats.setSuccessRate(totalAttempts > 0 ? (double) correctAttempts / totalAttempts * 100 : 0);
        stats.setUniqueAssessors(uniqueAssessors.size());

        return stats;
    }

    private boolean compareSelections(Map<String, String> correct, Map<String, String> user) {
        System.out.println("Comparing selections:");
        System.out.println("Correct answers: " + correct);
        System.out.println("User answers: " + user);

        // First check if all required selections are present
        for (Map.Entry<String, String> entry : correct.entrySet()) {
            String correctValue = entry.getValue();
            String userValue = user.get(entry.getKey());
            
            // Skip empty values in correct answer
            if (correctValue == null || correctValue.isEmpty()) {
                System.out.println("Skipping empty correct value for: " + entry.getKey());
                continue;
            }
            
            // If user doesn't have the key or value doesn't match
            if (userValue == null || !correctValue.equals(userValue)) {
                System.out.println("Mismatch found - Key: " + entry.getKey() + 
                    ", Correct: " + correctValue + ", User: " + userValue);
                return false;
            }
        }
        
        // Check that user hasn't selected anything that should be unselected
        for (Map.Entry<String, String> entry : user.entrySet()) {
            String userValue = entry.getValue();
            String correctValue = correct.get(entry.getKey());
            
            // Skip empty values in user answer
            if (userValue == null || userValue.isEmpty()) {
                System.out.println("Skipping empty user value for: " + entry.getKey());
                continue;
            }
            
            // If this key is not in correct answers or has different value
            if (correctValue == null || !userValue.equals(correctValue)) {
                System.out.println("Extra selection found - Key: " + entry.getKey() + 
                    ", User: " + userValue + ", Correct: " + correctValue);
                return false;
            }
        }
        
        System.out.println("Answers match!");
        return true;
    }

    public List<AssessorEducationalStatDTO> getEducationalAssessorStatistics(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (!batch.getIsEducational()) {
            throw new RuntimeException("This is not an educational batch");
        }

        List<Task> tasks = taskRepository.getAllByBatchId(batch);
        Map<Long, AssessorEducationalStatDTO> assessorStats = new HashMap<>();

        for (Task task : tasks) {
            List<UserMarkup> markups = userMarkupRepository.findByTaskId(task.getId());
            
            for (UserMarkup markup : markups) {
                Long assessorId = markup.getAssessor().getId();
                AssessorEducationalStatDTO stats = assessorStats.computeIfAbsent(assessorId, k -> {
                    AssessorEducationalStatDTO dto = new AssessorEducationalStatDTO();
                    dto.setLogin(markup.getAssessor().getLogin());
                    dto.setTotalTasks(0);
                    dto.setCorrectAnswers(0);
                    return dto;
                });

                stats.setTotalTasks(stats.getTotalTasks() + 1);

                // Get correct answer for this task
                String correctAnswerJson = batch.getCorrectAnswers().get(task.getId());
                if (correctAnswerJson != null) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, String> correctAnswer = mapper.readValue(correctAnswerJson, new TypeReference<Map<String, String>>() {});
                        
                        // Compare with user's answer
                        if (compareSelections(correctAnswer, markup.getSelections())) {
                            stats.setCorrectAnswers(stats.getCorrectAnswers() + 1);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing correct answer for task " + task.getId() + ": " + e.getMessage());
                    }
                }
            }
        }

        // Calculate rates for each assessor
        for (AssessorEducationalStatDTO stats : assessorStats.values()) {
            if (stats.getTotalTasks() > 0) {
                int incorrectAnswers = stats.getTotalTasks() - stats.getCorrectAnswers();
                stats.setErrorRate(100.0 * incorrectAnswers / stats.getTotalTasks());
                stats.setCompletionRate(100.0 * stats.getTotalTasks() / tasks.size());
            }
        }

        return new ArrayList<>(assessorStats.values());
    }

    public AssessorEducationalStatDTO getEducationalAssessorStatistic(Long batchId, Long assessorId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (!batch.getIsEducational()) {
            throw new RuntimeException("This is not an educational batch");
        }

        User assessor = userRepository.findById(assessorId)
                .orElseThrow(() -> new RuntimeException("Assessor not found"));

        List<Task> tasks = taskRepository.getAllByBatchId(batch);
        AssessorEducationalStatDTO stats = new AssessorEducationalStatDTO();
        stats.setLogin(assessor.getLogin());
        stats.setTotalTasks(0);
        stats.setCorrectAnswers(0);

        for (Task task : tasks) {
            Optional<UserMarkup> markupOpt = userMarkupRepository.findByTaskIdAndAssessorId(task.getId(), assessorId);
            if (markupOpt.isPresent()) {
                UserMarkup markup = markupOpt.get();
                stats.setTotalTasks(stats.getTotalTasks() + 1);

                String correctAnswerJson = batch.getCorrectAnswers().get(task.getId());
                if (correctAnswerJson != null) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, String> correctAnswer = mapper.readValue(correctAnswerJson, new TypeReference<Map<String, String>>() {});
                        
                        if (compareSelections(correctAnswer, markup.getSelections())) {
                            stats.setCorrectAnswers(stats.getCorrectAnswers() + 1);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing correct answer for task " + task.getId() + ": " + e.getMessage());
                    }
                }
            }
        }

        if (stats.getTotalTasks() > 0) {
            int incorrectAnswers = stats.getTotalTasks() - stats.getCorrectAnswers();
            stats.setErrorRate(100.0 * incorrectAnswers / stats.getTotalTasks());
            stats.setCompletionRate(100.0 * stats.getTotalTasks() / tasks.size());
        }

        return stats;
    }
}
