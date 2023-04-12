package com.dp.gantt.persistence.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhaseDto {

    private Long workId;
    private String name;
    private List<TaskDto> tasks = new ArrayList<>();
    private Long project;

    public PhaseDto(Long id, String name){
        this.workId = id;
        this.name = name;
    }

    public void addTask(TaskDto taskDto){
        this.tasks.add(taskDto);
    }
}
