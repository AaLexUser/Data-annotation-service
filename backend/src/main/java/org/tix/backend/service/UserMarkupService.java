package org.tix.backend.service;

import org.springframework.stereotype.Service;
import org.tix.backend.model.Task;
import org.tix.backend.model.User;
import org.tix.backend.model.UserMarkup;
import org.tix.backend.repository.TaskRepository;
import org.tix.backend.repository.UserMarkupRepository;
import org.tix.backend.repository.UserRepository;

import java.util.*;

@Service
public class UserMarkupService {
    private final UserMarkupRepository userMarkupRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public UserMarkupService(UserMarkupRepository userMarkupRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.userMarkupRepository = userMarkupRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public void submitMarkup(Long taskId, Long assessorId, Map<String, String> selections) throws RuntimeException {
        Optional<UserMarkup> existing = userMarkupRepository.findByTaskIdAndAssessorId(taskId, assessorId);
        if (existing.isPresent()) {
            throw new RuntimeException("Ассессор уже аннотировал эту задачу!");
        }
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User assessor = userRepository.findById(assessorId)
                .orElseThrow(() -> new RuntimeException("Assessor not found"));
        UserMarkup userMarkup = new UserMarkup();
        userMarkup.setTask(task);
        userMarkup.setAssessor(assessor);
        userMarkup.setSelections(selections);

        userMarkupRepository.save(userMarkup);

        task.setCurrentOverlaps(task.getCurrentOverlaps() + 1);
        taskRepository.save(task);
        if (Objects.equals(task.getCurrentOverlaps(), task.getFiniteOverlaps())) {
            finalizeTask(task.getId());
        }
    }
    private void finalizeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Map<String, String> finalMarkup = calculateFinalMarkup(taskId);
        task.setIsFinished(true);
        task.setFinalMarkup(finalMarkup.toString());
        taskRepository.save(task);
    }

    private Map<String, String> calculateFinalMarkup(Long taskId) {
        List<UserMarkup> markups = userMarkupRepository.findByTaskId(taskId);

        //  field_name → (значение → количество голосов)
        Map<String, Map<String, Long>> fieldVotes = new HashMap<>();

        // Подсчет количества голосов для каждого значения
        for (UserMarkup markup : markups) {
            for (Map.Entry<String, String> entry : markup.getSelections().entrySet()) {
                fieldVotes
                        .computeIfAbsent(entry.getKey(), k -> new HashMap<>()) // Если ключа нет, создаем новый Map
                        .merge(entry.getValue(), 1L, Long::sum); // Увеличиваем счетчик голосов
            }
        }

        // Определяем финальный результат (выбираем самое популярное значение)
        Map<String, String> finalMarkup = new HashMap<>();
        for (Map.Entry<String, Map<String, Long>> field : fieldVotes.entrySet()) {
            String mostVoted = field.getValue().entrySet().stream()
                    .max(Map.Entry.comparingByValue()) // Находим значение с макс. голосами
                    .map(Map.Entry::getKey) // Получаем ключ (само значение)
                    .orElse(null);
            finalMarkup.put(field.getKey(), mostVoted);
        }

        return finalMarkup;
    }
}
