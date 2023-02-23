package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.ProjectManager;
import com.dp.gantt.persistence.model.Task;
import com.dp.gantt.persistence.model.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private ProjectManager manager;
    private Integer resources;
    private List<TeamMember> members;
}
