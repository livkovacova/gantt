package com.dp.gantt.controller;

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
}
