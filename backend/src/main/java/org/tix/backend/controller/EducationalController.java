package org.tix.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tix.backend.dto.EducationalBatchDTO;
import org.tix.backend.service.EducationalService;

@RestController
@RequestMapping("/api/v1/educational")
public class EducationalController {
    private final EducationalService educationalService;

    public EducationalController(EducationalService educationalService) {
        this.educationalService = educationalService;
    }

    @PostMapping("/set-batch")
    public ResponseEntity<?> setEducationalBatch(@RequestBody EducationalBatchDTO dto) {
        try {
            educationalService.setEducationalBatch(dto);
            return ResponseEntity.ok("Educational batch set successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-answers")
    public ResponseEntity<?> checkAnswers(@RequestParam Long taskId, @RequestParam Long assessorId) {
        try {
            return ResponseEntity.ok(educationalService.checkAnswers(taskId, assessorId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 