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
//
//    private final ProjectRepository projectRepository;
//
//    private final TaskMapper taskMapper;
//
    private final GanttUserService ganttUserService;
//
//    private final ProjectService projectService;
//
//    public Task saveTask(TaskDto taskDto){
//        Task task = taskMapper.taskDtoToTask(taskDto);
//        updateDependenciesInTask(taskDto, task);
//        return taskRepository.save(task);
//    }
//
//    public List<Task> saveAllTasks(List<TaskDto> taskDtos){
//        List<Task> tasks = new ArrayList<>();
//        taskDtos.forEach((taskDto) -> {
//            Task task = taskMapper.taskDtoToTask(taskDto);
//            updateDependenciesInTask(taskDto, task);
//            tasks.add(task);
//        });
//        return taskRepository.saveAll(tasks);
//    }
//
    public Task getTask(Optional<Long> id){
        return taskRepository.findById(id.orElse(-1L))
                .orElseThrow(() -> {
                    log.error("Task with id = {} can not be find while getting task", id);
                    throw new TaskNotFoundException(id.orElse(-1L));
                });
    }
//
//    public Task updateTask(TaskDto taskDto){
//        Long updateTaskId = taskDto.getId();
//        Task taskToUpdate = taskRepository.findById(updateTaskId)
//                .orElseThrow(() -> {
//                    log.error("Task with id = {} can not be find while getting task", updateTaskId);
//                    throw new TaskNotFoundException(updateTaskId);
//                });
//        Task updateTask = taskMapper.taskDtoToTask(taskDto);
//        taskMapper.update(taskToUpdate, updateTask);
//        updateDependenciesInTask(taskDto, taskToUpdate);
//        return taskRepository.save(taskToUpdate);
//    }
//
//    public void removeTask(Long id){
//        taskRepository.delete(
//                taskRepository.findById(id)
//                        .orElseThrow(() -> {
//                            log.error("Task with id = {} can not be find while getting task", id);
//                            throw new TaskNotFoundException(id);
//                        })
//        );
//    }
//
    public Task updateDependenciesInTask(TaskDto updateTask, Optional<Long> id, Long ganttChartId){
        //Project project = updateTask.getGanttChartId() == null ? null : projectService.findProject(updateTask.getProjectId());
        Task taskToUpdate = getTask(id);
        List<GanttUser> assignees = updateTask.getAssignees() == null ? null : ganttUserService.findGanttUsers(updateTask.getAssignees());
        List<Task> dependencies = updateTask.getPredecessors() == null ? null : findTaskDependencies(updateTask.getPredecessors(), ganttChartId);

        //taskToUpdate.setProject(project);
        taskToUpdate.setAssignees(assignees);
        taskToUpdate.setPredecessors(dependencies);
        return taskToUpdate;
    }
////
//    private void updateDependenciesInTaskDto(Task updateTask, TaskDto taskToUpdate){
//        List<Long> assigneesIds = new ArrayList<>();
//        List<Long> dependenciesIds = new ArrayList<>();
//
//        updateTask.getAssignees().forEach(assignee -> assigneesIds.add(assignee.getId()));
//        updateTask.getPredecessors().forEach(dependency -> dependenciesIds.add(dependency.getId()));
//
//        taskToUpdate.setAssignees(assigneesIds);
//        taskToUpdate.setPredecessors(dependenciesIds);
//    }
//
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
