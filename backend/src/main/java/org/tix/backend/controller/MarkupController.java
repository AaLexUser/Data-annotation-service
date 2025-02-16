package org.tix.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tix.backend.dto.MarkupDTO;
import org.tix.backend.service.MarkupService;

@RestController
@RequestMapping("/api/v1/markup")
public class MarkupController {
    private final MarkupService markupService;

    public MarkupController(MarkupService markupService) {
        this.markupService = markupService;
    }

    @PostMapping("/load")
    public ResponseEntity<?> loadMarkup(@RequestBody MarkupDTO markupDTO) {

        return ResponseEntity.ok(markupService.loadMarkup(markupDTO));
    }
    @GetMapping("/byBatchId")
    public ResponseEntity<?> getMarkup(@RequestParam("id") Long id) {
        return ResponseEntity.ok(markupService.getMarkupByBatchId(id));
    }
}
