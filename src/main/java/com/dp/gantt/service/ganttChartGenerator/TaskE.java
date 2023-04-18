package com.dp.gantt.service.ganttChartGenerator;
import com.dp.gantt.persistence.model.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TaskE {
    private Long id;
    private int duration;

    private String name;
    private List<Predecessor> predecessors;
    private Instant startDate;
    private Instant endDate;

    private List<Long> assignees;

    private TaskPriority priority;

    private Instant minimalStartDate;

    private Integer addedGapDays = 0;

    private PhaseInfo phaseInfo;

    private Integer resources;

    public TaskE(Long id, int duration, List<Predecessor> predecessors) {
        this.id = id;
        this.duration = duration;
        this.predecessors = predecessors;
    }

    public TaskE(Long id, int duration, String name, List<Predecessor> predecessors, List<Long> assignees, TaskPriority priority, Long phaseId, String phaseName, Integer resources) {
        this.id = id;
        this.duration = duration;
        this.name = name;
        this.predecessors = predecessors;
        this.assignees = assignees;
        this.priority = priority;
        this.phaseInfo = new PhaseInfo(phaseId,phaseName);
        this.resources = resources;
    }

    public void addPredecessor(Predecessor predecessor){
        this.predecessors.add(predecessor);
    }
}
