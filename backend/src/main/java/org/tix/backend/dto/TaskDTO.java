package org.tix.backend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TaskDTO {
    private Long id;
    private Long batchId;
    private Map<String, String> rowFromBatch;
}
