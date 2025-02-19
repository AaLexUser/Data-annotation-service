package org.tix.backend.dto.stat;

import lombok.Data;

@Data
public class AssessorEducationalStatDTO {
    private String login;
    private int totalTasks;
    private int correctAnswers;
    private double errorRate;
    private double completionRate;
} 