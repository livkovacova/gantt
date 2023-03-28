package com.dp.gantt.service;

import com.dp.gantt.persistence.model.Role;
import com.dp.gantt.persistence.model.RoleType;
import com.dp.gantt.persistence.model.dto.GanttUserDto;
import com.dp.gantt.persistence.repository.GanttUserRepository;
import com.dp.gantt.persistence.repository.RoleRepository;
import com.dp.gantt.service.mapper.GanttUserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    @Autowired
    private GanttUserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    private final GanttUserMapper ganttUserMapper;

    public List<GanttUserDto> findAllByRole(RoleType roleType){
        Role role = roleRepository.findByName(roleType).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        return ganttUserMapper.ganttUserListToGanttUserDtoList(userRepository.findAllByRolesContains(role));
    }
}
