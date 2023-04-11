package com.dp.gantt.service.example;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TaskE {
    private int id;
    private int duration;
    private List<Predecessor> predecessors;
    private Instant startDate;
    private Instant endDate;

    private List<Integer> assignees;

    private Priority priority;

    private boolean extendable;

    private Instant minimalStartDate;

    private Integer addedGapDays = 0;

    public TaskE(Integer id, Integer duration, List<Predecessor> predecessors) {
        this.id = id;
        this.duration = duration;
        this.predecessors = predecessors;
    }

    public TaskE(Integer id, Integer duration, List<Predecessor> predecessors, List<Integer> assignees, Priority priority, boolean extendable) {
        this.id = id;
        this.duration = duration;
        this.predecessors = predecessors;
        this.assignees = assignees;
        this.priority = priority;
        this.extendable = extendable;
    }

    public void addPredecessor(Predecessor predecessor){
        this.predecessors.add(predecessor);
    }
}
