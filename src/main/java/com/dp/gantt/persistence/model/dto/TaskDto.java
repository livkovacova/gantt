package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long id;
    private String name;
    private TaskPriority priority;
    private Long duration;
    private Integer state;
    private Integer resources;
    private Boolean thirdParty;
    private Long ganttChartId;
    private List<Long> dependencies;
    private List<Long> assignees;

}
