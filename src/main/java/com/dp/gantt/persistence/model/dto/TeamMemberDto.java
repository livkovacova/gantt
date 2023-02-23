package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDto {
    private Long id;
    private String name;
    private List<Task> tasks;
    private List<Project> projects;
}
