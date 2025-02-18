package org.tix.backend.service;


import org.springframework.stereotype.Service;
import org.tix.backend.dto.TaskDTO;
import org.tix.backend.dto.mapper.TaskMapper;
import org.tix.backend.model.Task;
import org.tix.backend.repository.BatchRepository;
import org.tix.backend.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final BatchRepository batchRepository;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, BatchRepository batchRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.batchRepository = batchRepository;
    }

    public List<TaskDTO> getAvailableTasksForAssessor(Long batchId) {

       List<Task> taskList =  taskRepository.getAllByBatchId(batchRepository.findById(batchId).orElseThrow());
       return taskList.stream().map(taskMapper::toDTO).toList();
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Task> tasksPage = taskRepository.findByAssessorId(assessorId, pageable);
//        return tasksPage.getContent().stream().map(task -> taskMapper.toDTO(task)).collect(Collectors.toList());
    }
}
