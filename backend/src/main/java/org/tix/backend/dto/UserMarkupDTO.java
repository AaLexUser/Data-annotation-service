package org.tix.backend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class UserMarkupDTO {
    private Long taskId;
    private Long assessorId;
    private Map<String, String> selections;
}
