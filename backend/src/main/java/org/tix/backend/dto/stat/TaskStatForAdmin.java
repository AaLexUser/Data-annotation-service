package org.tix.backend.dto.stat;

import lombok.Data;

@Data
public class TaskStatForAdmin {

    private Long id;
    private String status;
    private String finalMark;
}
