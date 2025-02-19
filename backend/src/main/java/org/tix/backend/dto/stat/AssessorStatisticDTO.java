package org.tix.backend.dto.stat;

import lombok.Data;
import org.tix.backend.dto.TaskDTO;

import java.util.List;
import java.util.Map;

@Data
public class AssessorStatisticDTO {
    private Map<String,Integer> markupsStatistic;
    private List<TaskStatForAdmin> stats;


}
