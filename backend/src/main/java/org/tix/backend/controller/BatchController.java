package org.tix.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tix.backend.model.Batch;
import org.tix.backend.service.BatchService;

@RestController
@RequestMapping("/v1/batch")
public class BatchController {
    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }


    @PostMapping("/load")
    public ResponseEntity<?> loadBatch(Batch batch) {

        return ResponseEntity.ok(batchService.loadBatch(batch));
    }
    @PostMapping("/all")
    public ResponseEntity<?> showAllBatches(Batch batch) {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

}
