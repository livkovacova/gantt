package com.dp.gantt.controller;

import com.dp.gantt.persistence.model.RoleType;
import com.dp.gantt.persistence.model.dto.GanttUserDto;
import com.dp.gantt.persistence.model.dto.ProjectDto;
import com.dp.gantt.persistence.repository.GanttUserRepository;
import com.dp.gantt.persistence.repository.RoleRepository;
import com.dp.gantt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/allTeamMembers")
    public List<GanttUserDto> getAllTeamMembers(){
        return userService.findAllByRole(RoleType.TEAM_MEMBER);
    }
}
