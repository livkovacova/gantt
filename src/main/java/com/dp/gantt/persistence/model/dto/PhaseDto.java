package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhaseDto {

    private Long id;
    private String name;
    private List<Long> tasks;
    private Long project;
}
