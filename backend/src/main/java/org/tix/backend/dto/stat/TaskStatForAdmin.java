package org.tix.backend.dto.stat;

import lombok.Data;
import org.tix.backend.dto.TaskDTO;

import java.util.List;
import java.util.Map;

@Data
public class TaskStatForAdmin {

    private Long id;
    private String status;
    private String finalMark;
    private String batchName;
    private String finishTime;
}
