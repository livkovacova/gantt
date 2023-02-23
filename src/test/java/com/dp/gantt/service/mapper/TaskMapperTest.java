package com.dp.gantt.service.mapper;

import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.Task;
import com.dp.gantt.persistence.model.TaskPriority;
import com.dp.gantt.persistence.model.dto.TaskDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.BDDAssertions.then;
public class TaskMapperTest {

    private static TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
    @Test
    void test_taskToTaskDto() {
        Project project = new Project(1L, "projectName", "description", null, 2, null);
        Task task = new Task(1L, "taskName", TaskPriority.LOW, 0L, 1, 2, true, project, null, null);

        TaskDto result = taskMapper.taskToTaskDto(task);

        then(result).isNotNull();
        System.out.println(result);
    }
}