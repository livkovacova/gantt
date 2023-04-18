package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long workId;
    private String name;
    private TaskPriority priority;
    private Integer duration;
    private Integer resources;
    private List<Long> predecessors;
    private List<Long> assignees;
    private Instant startDate;
    private Instant endDate;
    private Optional<Long> realId;


    public TaskDto(Long workId, String name, TaskPriority priority, Integer duration, Integer resources, List<Long> predecessors, List<Long> assignees, Instant startDate, Instant endDate) {
        this.workId = workId;
        this.name = name;
        this.priority = priority;
        this.duration = duration;
        this.resources = resources;
        this.predecessors = predecessors;
        this.assignees = assignees;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
