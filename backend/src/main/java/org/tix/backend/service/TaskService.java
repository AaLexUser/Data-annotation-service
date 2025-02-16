package org.tix.backend.service;


import org.springframework.stereotype.Service;
import org.tix.backend.dto.TaskDTO;
import org.tix.backend.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskDTO> getAvailableTasksForAssessor(Long assessorId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Task> tasksPage = taskRepository.findByAssessorId(assessorId, pageable);
//        return tasksPage.getContent().stream().map(task -> taskMapper.toDTO(task)).collect(Collectors.toList());
        return null;
    }
}
