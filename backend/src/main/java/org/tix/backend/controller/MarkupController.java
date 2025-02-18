package org.tix.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tix.backend.dto.MarkupDTO;
import org.tix.backend.dto.UserMarkupDTO;
import org.tix.backend.service.MarkupService;
import org.tix.backend.service.UserMarkupService;

@RestController
@RequestMapping("/api/v1/markup")
public class MarkupController {
    private final MarkupService markupService;
    private final UserMarkupService userMarkupService;

    public MarkupController(MarkupService markupService, UserMarkupService userMarkupService) {
        this.markupService = markupService;
        this.userMarkupService = userMarkupService;
    }

    @PostMapping("/load")
    public ResponseEntity<?> loadMarkup(@RequestBody MarkupDTO markupDTO) {

        return ResponseEntity.ok(markupService.loadMarkup(markupDTO));
    }
    @GetMapping("/byBatchId")
    public ResponseEntity<?> getMarkup(@RequestParam("id") Long id) {
        return ResponseEntity.ok(markupService.getMarkupByBatchId(id));
    }

    @PostMapping("/submitUserMarkup")
    public ResponseEntity<?> submitUserMarkup(@RequestBody UserMarkupDTO userMarkupDTO) {
        try {
            userMarkupService.submitMarkup(userMarkupDTO.getTaskId(), userMarkupDTO.getAssessorId(), userMarkupDTO.getSelections());
            return ResponseEntity.ok("Разметка сохранена");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
