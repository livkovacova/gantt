package com.dp.gantt.service;

import com.dp.gantt.exceptions.ProjectNotFoundException;
import com.dp.gantt.exceptions.TaskNotFoundException;
import com.dp.gantt.model.PageResponse;
import com.dp.gantt.persistence.model.*;
import com.dp.gantt.persistence.model.dto.ProjectRequestDto;
import com.dp.gantt.persistence.model.dto.ProjectResponseDto;
import com.dp.gantt.persistence.repository.GanttChartRepository;
import com.dp.gantt.persistence.repository.PhaseRepository;
import com.dp.gantt.persistence.repository.ProjectRepository;
import com.dp.gantt.persistence.repository.TaskRepository;
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

    private final ProjectMapper projectMapper;

    private final GanttUserService ganttUserService;

    private final GanttChartRepository ganttChartRepository;

    private final TaskRepository taskRepository;

    private final PhaseRepository phaseRepository;

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
        project.setDependencyCreated(false);
        return projectRepository.save(project);
    }

    public Project updateProject(ProjectRequestDto projectRequestDto){
        Long updateProjectId = projectRequestDto.getId();
        Project projectToUpdate = projectRepository.findById(updateProjectId)
                .orElseThrow(() -> {
                    log.error("Project with id = {} can not be find while getting task", updateProjectId);
                    throw new ProjectNotFoundException(updateProjectId);
                });
        GanttChart ganttChart = projectToUpdate.getGanttChart();
        boolean depCreated = projectToUpdate.getDependencyCreated();
        Project updateProject = projectMapper.projectRequestDtoToProject(projectRequestDto);
        projectMapper.update(projectToUpdate, updateProject);
        updateDependenciesInProject(projectRequestDto, projectToUpdate);
        if(ganttChart != null){
            projectToUpdate.setGanttChart(ganttChart);
        }
        projectToUpdate.setDependencyCreated(depCreated);
        return projectRepository.save(projectToUpdate);
    }

    private void updateDependenciesInProject(ProjectRequestDto projectRequestDto, Project project){
        GanttUser manager = projectRequestDto.getManager() == null ? null : ganttUserService.findGanttUser(projectRequestDto.getManager());
        List<GanttUser> members = projectRequestDto.getMembers() == null ? null : ganttUserService.findGanttUsers(projectRequestDto.getMembers());

        project.setManager(manager);
        project.setMembers(members);
    }

    private void updateDependenciesInProjectResponse(ProjectResponseDto projectResponseDto, Project project){
        GanttUser manager = project.getManager();
        List<GanttUser> members = project.getMembers();

        projectResponseDto.setManager(manager);
        projectResponseDto.setMembers(members);
    }

    public Project findProject(Long projectId){
        return projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project with id = {} can not be find", projectId);
                    throw new ProjectNotFoundException(projectId);
                });
    }

    public ProjectResponseDto findProjectForResponse(Long projectId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project with id = {} can not be find", projectId);
                    throw new ProjectNotFoundException(projectId);
                });
        ProjectResponseDto projectResponseDto = projectMapper.projectToProjectDto(project);
        projectResponseDto.setTreeCreated(project.getDependencyCreated());
        if(project.getGanttChart() != null){
            projectResponseDto.setGanttCreated(true);
        }
        updateDependenciesInProjectResponse(projectResponseDto, project);
        return projectResponseDto;
    }

    public Project deleteProject(Long projectId){
        Project projectToDelete = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project with id = {} can not be find", projectId);
                    throw new ProjectNotFoundException(projectId);
                });

        if(projectToDelete.getGanttChart() != null){
            Long ganttChartId = projectToDelete.getGanttChart().getId();
            deleteGanttChart(ganttChartId);
            projectToDelete.setGanttChart(null);
        }
        projectToDelete.setManager(null);
        projectToDelete.setMembers(null);

        projectRepository.save(projectToDelete);
        projectRepository.deleteById(projectId);
        return null;
    }

    public void deleteGanttChart(Long id){
        List<Phase> phases = phaseRepository.findAllByGanttChart_Id(id);
        phases.forEach(phase -> {
            List<Task> tasks = taskRepository.findAllByPhase_IdAndPhase_GanttChart_Id(phase.getId(), id);
            tasks.forEach(task -> {
                task.setPhase(null);
                taskRepository.save(task);
                taskRepository.deleteById(task.getId());
            });
            phase.setGanttChart(null);
            phaseRepository.save(phase);
            phaseRepository.deleteById(phase.getId());
        });
        GanttChart ganttChart = ganttChartRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        ganttChart.setProject(null);
        ganttChartRepository.save(ganttChart);
        ganttChartRepository.deleteById(id);
    }

    public void setDependency(Long projectId){
        Project projectToChange = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project with id = {} can not be find", projectId);
                    throw new ProjectNotFoundException(projectId);
                });
        projectToChange.setDependencyCreated(true);
        projectRepository.save(projectToChange);
    }
}
