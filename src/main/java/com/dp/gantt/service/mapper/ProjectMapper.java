package com.dp.gantt.service.mapper;

import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.dto.ProjectDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto projectToProjectDto(Project project);

    List<ProjectDto> projectListToProjectDtoList(List<Project> projects);
}
