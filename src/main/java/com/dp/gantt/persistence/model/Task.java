package com.dp.gantt.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    private Long workId;
    private String name;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    private Integer duration;
    private Integer resources;

    @ManyToOne
    @JoinColumn(name = "phase_id")
    private Phase phase;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Task> predecessors;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "task_assignees",
            joinColumns = {@JoinColumn(name = "task_id")},
            inverseJoinColumns = {@JoinColumn(name = "gantt_user_id")}
    )
    private List<GanttUser> assignees;

    private Instant startDate;

    private Instant endDate;

}
