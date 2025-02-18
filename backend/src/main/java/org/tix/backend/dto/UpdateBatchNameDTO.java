package org.tix.backend.dto;

import lombok.Data;

@Data
public class UpdateBatchNameDTO {
    private Long batchId;
    private String name;
}
