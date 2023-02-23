package com.dp.gantt.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "team_member")
public class TeamMember extends GanttUser {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "assignees")
    @JsonIgnore
    private Set<Task> tasks;

    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private List<Project> projects;

    @Override
    void printHello() {
        System.out.println("Im team member " + this.name);
    }
}
