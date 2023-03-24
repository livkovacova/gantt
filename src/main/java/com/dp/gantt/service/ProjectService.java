package com.dp.gantt.service;

import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.RoleType;
import com.dp.gantt.persistence.model.dto.ProjectDto;
import com.dp.gantt.persistence.repository.ProjectRepository;
import com.dp.gantt.service.mapper.ProjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    public List<ProjectDto> getUsersProjects(Long id, RoleType roleType){
        List<Project> result;
        if(roleType == RoleType.TEAM_MEMBER){
            result = projectRepository.findAllByMembers_id(id);
        }
        else{
            result = projectRepository.findAllByManager_id(id);
        }
        return projectMapper.projectListToProjectDtoList(result);
    }
}
