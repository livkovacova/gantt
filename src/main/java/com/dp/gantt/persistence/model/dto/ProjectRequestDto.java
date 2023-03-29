package com.dp.gantt.persistence.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {

    private Long id;
    private String name;
    private String description;
    private Long manager;
    private Integer resources;
    private List<Long> members;
    private Instant startDate;
}
