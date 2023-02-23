package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectManagerDto {
    private Long id;
    private String name;
    private List<Project> projects;
}
