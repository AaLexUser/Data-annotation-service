package org.tix.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tix.backend.dto.AssessorDTO;
import org.tix.backend.dto.BatchAssignmentDTO;
import org.tix.backend.dto.TaskDTO;
import org.tix.backend.service.TaskService;
import org.tix.backend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/assessor/all")
    public ResponseEntity<List<AssessorDTO>> getAllAssessors() {
        return ResponseEntity.ok(userService.getAllAssessors());
    }

    @PostMapping("/batch/assign")
    public ResponseEntity<?> assignBatchToAssessors(@RequestBody BatchAssignmentDTO assignmentDTO) {
        userService.assignBatchToAssessors(assignmentDTO.getBatchId(), assignmentDTO.getAssessorIds());
        return ResponseEntity.ok("Batch assigned successfully");
    }

    @GetMapping("/assessor/grantBatch")
    public ResponseEntity<?> grantBatchToAssessor(@RequestParam Long batchId, @RequestParam Long userId) {
        userService.grantAssessorToBatch(batchId, userId);
        return ResponseEntity.ok("Granted successfully");
    }

    @GetMapping("/assessor/refuseBatch")
    public ResponseEntity<?> refuseBatchToAssessor(@RequestParam Long batchId, @RequestParam Long userId) {
        userService.refuseBatchToAssessor(batchId, userId);
        return ResponseEntity.ok("Refuse successfully");
    }
}
