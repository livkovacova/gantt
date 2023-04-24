package com.dp.gantt.controller;

import com.dp.gantt.model.PageResponse;
import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.RoleType;
import com.dp.gantt.persistence.model.dto.ProjectRequestDto;
import com.dp.gantt.persistence.model.dto.ProjectResponseDto;
import com.dp.gantt.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/byUser")
    public List<ProjectResponseDto> getProjectsByUserId(@RequestParam Long userId, @RequestParam RoleType role){
        return projectService.getUsersProjects(userId, role);
    }

    @GetMapping("/byUserPaged")
    public PageResponse<ProjectResponseDto> getProjectsByUserId(@RequestParam Long userId, @RequestParam RoleType role, @RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer size,
                                                                @RequestParam(defaultValue = "id", required = false) String orderBy,
                                                                @RequestParam(defaultValue = "DESC", required = false) String direction){
        return projectService.getUsersProjects(userId, role, page, size, orderBy, direction);
    }

    @GetMapping("/project")
    public ProjectResponseDto getProjectsById(@RequestParam Long id){
        return projectService.findProjectForResponse(id);
    }

    @PostMapping("/save")
    public Project createProject(@Valid @RequestBody ProjectRequestDto projectRequestDto){
        return projectService.saveProject(projectRequestDto);
    }

    @PostMapping("/update")
    public Project updateProject(@Valid @RequestBody ProjectRequestDto projectRequestDto){
        return projectService.updateProject(projectRequestDto);
    }

    @DeleteMapping("/delete")
    public Project deleteProject(@RequestParam Long id) {
        return projectService.saveDeleteProject(id);
    }

    @GetMapping("/dependency")
    public void setDependency(@RequestParam Long id) {
        projectService.setDependency(id);
    }
}
