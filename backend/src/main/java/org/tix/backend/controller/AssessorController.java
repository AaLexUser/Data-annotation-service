package org.tix.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tix.backend.dto.TaskDTO;
import org.tix.backend.dto.UserMarkupDTO;
import org.tix.backend.service.TaskService;
import org.tix.backend.service.UserMarkupService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessor")
public class AssessorController {
    private final UserMarkupService userMarkupService;
    private final TaskService taskService;

    public AssessorController(UserMarkupService userMarkupService, TaskService taskService) {
        this.userMarkupService = userMarkupService;
        this.taskService = taskService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitUserMarkup(@RequestBody UserMarkupDTO userMarkup) {
        userMarkupService.submitMarkup(userMarkup.getTaskId(), userMarkup.getAssessorId(), userMarkup.getSelections());
        return ResponseEntity.ok("Разметка сохранена");
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDTO>> getTasks(
            @RequestParam("assessorId") Long assessorId,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        List<TaskDTO> tasks = taskService.getAvailableTasksForAssessor(assessorId, page, size);
        return ResponseEntity.ok(tasks);
    }

}
