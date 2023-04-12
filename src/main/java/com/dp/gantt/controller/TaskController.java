package com.dp.gantt.controller;

import com.dp.gantt.persistence.model.Task;
import com.dp.gantt.persistence.model.dto.TaskDto;
import com.dp.gantt.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {

//    @Autowired
//    private TaskService taskService;
//
//    @PostMapping("/saveTask")
//    public Task createTask(@Valid @RequestBody TaskDto taskDto){
//        return taskService.saveTask(taskDto);
//    }
//
//    @PostMapping("/saveAllTasks")
//    public List<Task> createTasks(@Valid @RequestBody List<TaskDto> taskDtos){
//        return taskService.saveAllTasks(taskDtos);
//    }
//
//    @GetMapping("/getTask")
//    public TaskDto getTask(@RequestParam Long id){
//        return taskService.getTask(id);
//    }

}
