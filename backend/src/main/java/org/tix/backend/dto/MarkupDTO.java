package org.tix.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class MarkupDTO {
    private List<FormData> data;
    private int batchId;


}
