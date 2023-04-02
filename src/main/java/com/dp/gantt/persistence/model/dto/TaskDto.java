package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.Task;
import com.dp.gantt.persistence.model.TaskPriority;
import com.dp.gantt.persistence.model.TeamMember;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private Long id;
    private String name;
    private TaskPriority priority;
    private Long duration;
    private Integer state;
    private Integer resources;
    private Boolean thirdParty;
    private Long ganttChartId;
    private List<Long> dependencies;
    private List<Long> assignees;

}
