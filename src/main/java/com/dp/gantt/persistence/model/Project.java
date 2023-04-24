package com.dp.gantt.persistence.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // important
public class Project {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    @Size(max = 30, min = 3)
    private String name;

    @Size(max = 200, min = 3)
    private String description;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private GanttUser manager;

    private Integer resources;

    private String currency;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    @JoinTable(
            name = "project_member",
            joinColumns = {@JoinColumn(name = "project_id")},
            inverseJoinColumns = {@JoinColumn(name = "gantt_user_id")}
    )
    private List<GanttUser> members;

    private Instant startDate;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "gantt_chart_id")
    private GanttChart ganttChart;

    private Boolean dependencyCreated;

    private Boolean active;

}
