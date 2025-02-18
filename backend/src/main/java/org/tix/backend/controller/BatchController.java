package org.tix.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tix.backend.dto.BatchDetailsDTO;
import org.tix.backend.model.Batch;
import org.tix.backend.service.BatchService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/v1/batch")
public class BatchController {
    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }


    @PostMapping("/load")
    public ResponseEntity<?> loadBatch(@RequestParam MultipartFile file,
                                       @RequestParam("overlaps") Integer overlaps,
                                       @RequestParam("batchName") String batchName) {
        try {
            Batch batch = batchService.saveBatch(file, overlaps, batchName);
            return ResponseEntity.ok(batch.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving batch");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> showAllBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    @GetMapping("/for-assessor")
    public ResponseEntity<?> showAllForAssessor(@RequestParam Long userId) {
        return ResponseEntity.ok(batchService.getAllBatchesForAssessor(userId));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getBatchStatus(@RequestParam Long batchId) {
        String status = batchService.getBatchStatus(batchId);
        return ResponseEntity.ok(status);
    }

    public ResponseEntity<BatchDetailsDTO> getBatchDetails(@RequestParam Long batchId) {
        return null;
    }
    @PostMapping("/{id}/toggle-status")
    public ResponseEntity<Map<String, String>> toggleBatchStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String newStatus = request.get("status");
        String updatedStatus = batchService.toggleBatchStatus(id, newStatus);

        return ResponseEntity.ok(Map.of("status", updatedStatus));
    }
    @GetMapping("/{batchId}/status")
    public ResponseEntity<?> getStatus(@PathVariable Long batchId) {
        return batchService.getCurrentStatus(batchId);
    }

}
