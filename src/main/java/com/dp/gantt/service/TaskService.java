package com.dp.gantt.service;

import com.dp.gantt.exceptions.TaskNotFoundException;
import com.dp.gantt.persistence.model.GanttUser;
import com.dp.gantt.persistence.model.Task;
import com.dp.gantt.persistence.model.dto.TaskDto;
import com.dp.gantt.persistence.repository.ProjectRepository;
import com.dp.gantt.persistence.repository.TaskRepository;
import com.dp.gantt.service.mapper.TaskMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final GanttUserService ganttUserService;

    public Task getTask(Optional<Long> id){
        return taskRepository.findById(id.orElse(-1L))
                .orElseThrow(() -> {
                    log.error("Task with id = {} can not be find while getting task", id);
                    throw new TaskNotFoundException(id.orElse(-1L));
                });
    }
    public Task updateDependenciesInTask(TaskDto updateTask, Optional<Long> id, Long ganttChartId){
        Task taskToUpdate = getTask(id);
        List<GanttUser> assignees = updateTask.getAssignees() == null ? null : ganttUserService.findGanttUsers(updateTask.getAssignees());
        List<Task> dependencies = updateTask.getPredecessors() == null ? null : findTaskDependencies(updateTask.getPredecessors(), ganttChartId);

        taskToUpdate.setAssignees(assignees);
        taskToUpdate.setPredecessors(dependencies);
        return taskToUpdate;
    }

    private List<Task> findTaskDependencies(List<Long> ids, Long ganttChartId){
        List<Task> dependencies = new ArrayList<>();
        for (Long id : ids){
            Task dependency = taskRepository.findByWorkIdAndAndPhase_GanttChart_Id(id, ganttChartId)
                    .orElseThrow(() -> {
                        log.error("Task with id = {} can not be find while saving task", id);
                        throw new TaskNotFoundException(id);
                    });
            dependencies.add(dependency);
        }
        return dependencies;
    }


}
