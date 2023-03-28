package com.dp.gantt.persistence.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    @Size(max = 30, min = 3)
    private String name;

    @Size(max = 200, min = 3)
    private String description;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private GanttUser manager;

    private Integer resources;

    @ManyToMany
    @JoinTable(
            name = "project_member",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "gantt_user_id")}
    )
    private List<GanttUser> members;

}
