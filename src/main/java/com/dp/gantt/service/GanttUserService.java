package com.dp.gantt.service;


import com.dp.gantt.exceptions.GanttUserNotFoundException;
import com.dp.gantt.persistence.model.GanttUser;
import com.dp.gantt.persistence.repository.GanttUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GanttUserService {

    private final GanttUserRepository ganttUserRepository;

    public List<GanttUser> findGanttUsers(List<Long> ids){
        List<GanttUser> assignees = new ArrayList<>();
        for (Long id : ids){
            GanttUser assignee = findGanttUser(id);
            assignees.add(assignee);
        }
        return assignees;
    }

    public GanttUser findGanttUser(Long userId){
        return ganttUserRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("GanttUser with id = {} can not be find while saving project", userId);
                    throw new GanttUserNotFoundException(userId);
                });
    }
}
