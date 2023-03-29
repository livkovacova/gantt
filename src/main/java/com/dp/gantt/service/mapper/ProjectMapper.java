package com.dp.gantt.service.mapper;

import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.dto.ProjectRequestDto;
import com.dp.gantt.persistence.model.dto.ProjectResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectResponseDto projectToProjectDto(Project project);

    List<ProjectResponseDto> projectListToProjectDtoList(List<Project> projects);

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "members", ignore = true)
    Project projectRequestDtoToProject(ProjectRequestDto projectRequestDto);

    @Mapping(target = "members", ignore = true)
    @Mapping(target = "manager", ignore = true)
    ProjectRequestDto projectToProjectRequestDto(Project project);

    @Mapping(target = "members", ignore = true)
    @Mapping(target = "manager", ignore = true)
    void update(@MappingTarget Project project, Project updateProject);
}
