package com.dp.gantt.persistence.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "manager")
public class ProjectManager {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

}
