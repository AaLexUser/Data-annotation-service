package org.tix.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tix.backend.dto.HoneypotTaskDTO;
import org.tix.backend.model.TaskAnswer;
import org.tix.backend.service.HoneypotService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/honeypot")
@RequiredArgsConstructor
public class HoneypotController {
    private final HoneypotService honeypotService;

    @PostMapping("/task")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskAnswer> createHoneypotTask(@RequestBody HoneypotTaskDTO dto) {
        return ResponseEntity.ok(honeypotService.createHoneypotTask(dto));
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskAnswer>> getHoneypotTasksByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(honeypotService.getHoneypotTasksByBatch(batchId));
    }

    @GetMapping("/check/{batchId}/{assessorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> checkAssessorAnswers(
            @PathVariable Long batchId,
            @PathVariable Long assessorId) {
        return ResponseEntity.ok(honeypotService.checkAssessorAnswers(batchId, assessorId));
    }
} 