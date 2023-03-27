package com.dp.gantt.controller;

import com.dp.gantt.model.PageResponse;
import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.RoleType;
import com.dp.gantt.persistence.model.dto.ProjectDto;
import com.dp.gantt.service.ProjectService;
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
    public List<ProjectDto> getProjectsByUserId(@RequestParam Long userId, @RequestParam RoleType role){
        return projectService.getUsersProjects(userId, role);
    }

    @GetMapping("/byUserPaged")
    public PageResponse<ProjectDto> getProjectsByUserId(@RequestParam Long userId, @RequestParam RoleType role, @RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        @RequestParam(defaultValue = "id", required = false) String orderBy,
                                                        @RequestParam(defaultValue = "ASC", required = false) String direction){
        return projectService.getUsersProjects(userId, role, page, size, orderBy, direction);
    }
}
