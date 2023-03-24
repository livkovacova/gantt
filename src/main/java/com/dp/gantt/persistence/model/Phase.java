package com.dp.gantt.persistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "phase")
public class Phase {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "phase")
    @JsonManagedReference
    private List<Task> tasks;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;
}
