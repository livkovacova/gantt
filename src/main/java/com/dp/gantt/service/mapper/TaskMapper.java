package com.dp.gantt.service.mapper;

import com.dp.gantt.persistence.model.Task;
import com.dp.gantt.persistence.model.dto.TaskDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

//    @Mapping(target = "dependencies", ignore = true)
//    @Mapping(target = "assignees", ignore = true)
//    TaskDto taskToTaskDto(Task task);
//
//    @Mapping(target = "dependencies", ignore = true)
//    @Mapping(target = "assignees", ignore = true)
//    Task taskDtoToTask(TaskDto taskDto);
//
//    @Mapping(target = "dependencies", ignore = true)
//    @Mapping(target = "assignees", ignore = true)
//    void update(@MappingTarget Task task, Task updateTask);
//
//    @Mapping(target = "dependencies", ignore = true)
//    @Mapping(target = "assignees", ignore = true)
//    List<TaskDto> taskListToTaskDtoList(List<Task> tasks);

}
