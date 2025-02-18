package org.tix.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tix.backend.dto.MarkupDTO;
import org.tix.backend.dto.UserMarkupDTO;
import org.tix.backend.model.Markup;
import org.tix.backend.service.MarkupService;
import org.tix.backend.service.UserMarkupService;

import java.util.List;

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
    public ResponseEntity<Markup> loadMarkup(@RequestBody MarkupDTO markupDTO) {
        return ResponseEntity.ok(markupService.createMarkup(markupDTO));
    }

    @GetMapping("/byBatchId")
    public ResponseEntity<Markup> getMarkup(@RequestParam("id") Long id) {
        return ResponseEntity.ok(markupService.getActiveMarkup(id));
    }

    @GetMapping("/versions/{batchId}")
    public ResponseEntity<List<Markup>> getMarkupVersions(@PathVariable Long batchId) {
        return ResponseEntity.ok(markupService.getMarkupHistory(batchId));
    }

    @PostMapping("/activate/{batchId}/{version}")
    public ResponseEntity<Markup> activateVersion(
            @PathVariable Long batchId,
            @PathVariable Integer version) {
        return ResponseEntity.ok(markupService.activateVersion(batchId, version));
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
