package org.tix.backend.service;

import org.springframework.stereotype.Service;
import org.tix.backend.dto.stat.AdminPageStatisticDTO;
import org.tix.backend.model.Batch;
import org.tix.backend.model.Task;
import org.tix.backend.repository.BatchRepository;
import org.tix.backend.repository.TaskRepository;
import org.tix.backend.repository.UserMarkupRepository;
import org.tix.backend.repository.UserRepository;

import java.text.DecimalFormat;
import java.util.List;

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

        Double percentOfCompletedTasks = (completedTask * 100.0 / taskByOverlaps)%100;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        adminPageStatisticDTO.setPercentOfCompletedTasks(decimalFormat.format(percentOfCompletedTasks));
        return adminPageStatisticDTO;
    }

}
