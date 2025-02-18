package org.tix.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tix.backend.model.Batch;
import org.tix.backend.service.BatchService;

import java.io.IOException;

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

}
