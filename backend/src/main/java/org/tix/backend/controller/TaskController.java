package org.tix.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.tix.backend.service.TaskService;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
}
