package org.tix.backend.dto;

import lombok.Data;

@Data
public class BatchDetailsDTO {
    private String name;
    private String status;
    private Long batchId;
}
