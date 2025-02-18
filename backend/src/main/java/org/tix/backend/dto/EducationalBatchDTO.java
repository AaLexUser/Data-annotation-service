package org.tix.backend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class EducationalBatchDTO {
    private Long batchId;
    private Map<Long, String> correctAnswers; // taskId -> correct answer in JSON format
} 