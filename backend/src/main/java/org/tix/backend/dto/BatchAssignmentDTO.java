package org.tix.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchAssignmentDTO {
    private Long batchId;
    private List<Long> assessorIds;
} 