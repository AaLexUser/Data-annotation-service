package org.tix.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<AdminPageStatisticDTO> getAdminPageStatistic(@RequestParam Long batchId) {
        return ResponseEntity.ok(statisticService.getAdminPageStatistic(batchId));

    }
}
