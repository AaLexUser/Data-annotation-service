package org.tix.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tix.backend.dto.stat.AdminPageStatisticDTO;
import org.tix.backend.service.StatisticService;

@RestController
@RequestMapping("/api/v1/stats")
public class StatisticController {
    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("/batch-preview")
    public ResponseEntity<?> getAdminPageStatistic(@RequestParam("batchId") Long batchId) {
        return ResponseEntity.ok(statisticService.getAdminPageStatistic(batchId));
    }

    @GetMapping("/educational")
    public ResponseEntity<?> getEducationalStatistics(@RequestParam("batchId") Long batchId) {
        try {
            return ResponseEntity.ok(statisticService.getEducationalStatistics(batchId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
