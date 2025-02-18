package org.tix.backend.dto.stat;

import lombok.Data;

@Data
public class EducationalStatisticDTO {
    private Long batchId;
    private String batchName;
    private int totalAttempts;
    private int correctAttempts;
    private double successRate;
    private int uniqueAssessors;
} 