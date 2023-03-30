package com.dp.gantt.service;

import com.dp.gantt.exceptions.ProjectNotFoundException;
import com.dp.gantt.model.PageResponse;
import com.dp.gantt.persistence.model.GanttUser;
import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.RoleType;
import com.dp.gantt.persistence.model.dto.ProjectRequestDto;
import com.dp.gantt.persistence.model.dto.ProjectResponseDto;
import com.dp.gantt.persistence.repository.GanttUserRepository;
import com.dp.gantt.persistence.repository.ProjectRepository;
import com.dp.gantt.service.mapper.ProjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    private final GanttUserRepository ganttUserRepository;

    private final ProjectMapper projectMapper;

    private final GanttUserService ganttUserService;

    public List<ProjectResponseDto> getUsersProjects(Long id, RoleType roleType){
        List<Project> result;
        if(roleType == RoleType.TEAM_MEMBER){
            result = projectRepository.findAllByMembers_id(id);
        }
        else{
            result = projectRepository.findAllByManager_id(id);
        }
        return projectMapper.projectListToProjectDtoList(result);
    }

    public PageResponse<ProjectResponseDto> getUsersProjects(Long id, RoleType roleType, Integer page, Integer size, String orderBy, String direction){
        Sort sorting = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable paging = PageRequest.of(page, size, sorting);
        Page<Project> projectPage;
        if(roleType == RoleType.TEAM_MEMBER){
            projectPage = projectRepository.findAllByMembers_id(id, paging);
        }
        else{
            projectPage = projectRepository.findAllByManager_id(id, paging);
        }
        List<ProjectResponseDto> projectResponseDtoList = projectMapper.projectListToProjectDtoList(projectPage.getContent());
        return new PageResponse<>(projectPage.getTotalElements(), projectPage.getTotalPages(), projectResponseDtoList);
    }

    public Project saveProject(ProjectRequestDto projectRequestDto){
        Project project = projectMapper.projectRequestDtoToProject(projectRequestDto);
        updateDependenciesInProject(projectRequestDto, project);
        return projectRepository.save(project);
    }

    public Project updateProject(ProjectRequestDto projectRequestDto){
        Long updateProjectId = projectRequestDto.getId();
        Project projectToUpdate = projectRepository.findById(updateProjectId)
                .orElseThrow(() -> {
                    log.error("Project with id = {} can not be find while getting task", updateProjectId);
                    throw new ProjectNotFoundException(updateProjectId);
                });
        Project updateProject = projectMapper.projectRequestDtoToProject(projectRequestDto);
        projectMapper.update(projectToUpdate, updateProject);
        updateDependenciesInProject(projectRequestDto, projectToUpdate);
        return projectRepository.save(projectToUpdate);
    }

    private void updateDependenciesInProject(ProjectRequestDto projectRequestDto, Project project){
        GanttUser manager = projectRequestDto.getManager() == null ? null : ganttUserService.findGanttUser(projectRequestDto.getManager());
        List<GanttUser> members = projectRequestDto.getMembers() == null ? null : ganttUserService.findGanttUsers(projectRequestDto.getMembers());

        project.setManager(manager);
        project.setMembers(members);
    }

    public Project findProject(Long projectId){
        return projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project with id = {} can not be find", projectId);
                    throw new ProjectNotFoundException(projectId);
                });
    }

    public Project deleteProject(Long projectId){
        Project projectToDelete = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project with id = {} can not be find", projectId);
                    throw new ProjectNotFoundException(projectId);
                });
        projectToDelete.setManager(null);
        projectToDelete.setMembers(null);
        projectRepository.save(projectToDelete);
        projectRepository.deleteById(projectId);
        return null;
    }
}
