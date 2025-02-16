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
    public ResponseEntity<?> loadBatch(@RequestParam("file") MultipartFile file, @RequestParam("overlaps") Integer overlaps) {
        try {
            Batch batch = batchService.saveBatch(file, overlaps);
            return ResponseEntity.ok(batch.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving batch");
        }
    }
    @PostMapping("/all")
    public ResponseEntity<?> showAllBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

}
