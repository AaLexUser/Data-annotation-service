package org.tix.backend.dto.mapper;

import org.springframework.stereotype.Component;
import org.tix.backend.dto.TaskDTO;
import org.tix.backend.model.Task;
import org.tix.backend.repository.BatchRepository;

@Component
public class TaskMapper {

    public TaskDTO toDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setBatchId(task.getBatchId().getId());
        taskDTO.setRowFromBatch(task.getRowFromBatch());
        return taskDTO;
    }
}
