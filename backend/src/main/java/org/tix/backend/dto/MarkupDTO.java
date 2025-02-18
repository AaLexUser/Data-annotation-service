package org.tix.backend.dto;

import lombok.Data;
import java.util.Map;

@Data
public class MarkupDTO {
    private Integer batchId;
    private Map<String, String> elements;
}
