package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long workId;
    private String name;
    private TaskPriority priority;
    private Integer duration;
    private Integer resources;
    private Boolean extendable;
    private List<Long> predecessors;
    private List<Long> assignees;
    private Instant startDate;
    private Instant endDate;

}
