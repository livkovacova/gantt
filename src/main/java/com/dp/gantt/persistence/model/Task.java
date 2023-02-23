package com.dp.gantt.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;
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
    private String name;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    private Long duration;
    @Min(0)
    @Max(100)
    private Integer state;
    private Integer resources;
    private Boolean thirdParty;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;

    @ManyToMany
    @JsonIgnore
    private List<Task> dependencies;

    @ManyToMany
    @JoinTable(
            name = "task_assignees",
            joinColumns = {@JoinColumn(name = "task_id")},
            inverseJoinColumns = {@JoinColumn(name = "team_member_id")}
    )
    @JsonIgnore
    private List<TeamMember> assignees;


}
