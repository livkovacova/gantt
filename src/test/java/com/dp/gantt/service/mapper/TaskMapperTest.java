package com.dp.gantt.service.mapper;

import com.dp.gantt.persistence.model.*;
import com.dp.gantt.persistence.model.dto.TaskDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.BDDAssertions.then;
public class TaskMapperTest {

    private static TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
    @Test
    void test_taskToTaskDto() {
        GanttChart ganttChart = new GanttChart(1L, null, null);
        Task task = new Task(1L, "taskName", TaskPriority.LOW, 1L, 1, 1, false, null, null, null);

        TaskDto result = taskMapper.taskToTaskDto(task);

        then(result).isNotNull();
        System.out.println(result);
    }
}