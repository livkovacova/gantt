package com.dp.gantt.persistence.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
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

    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @JsonBackReference
    private ProjectManager manager;

    private Integer resources;

    @ManyToMany
    @JoinTable(
            name = "project_member",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "team_member_id")}
    )
    @JsonIgnore
    private Set<TeamMember> members;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    @JsonManagedReference
    private List<Task> tasks;

}
