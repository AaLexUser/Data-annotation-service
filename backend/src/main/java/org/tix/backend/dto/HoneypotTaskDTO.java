package org.tix.backend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class HoneypotTaskDTO {
    private Long batchId;
    private Long taskId;
    private Map<String, String> correctAnswers; // key: markup field, value: correct answer
} 