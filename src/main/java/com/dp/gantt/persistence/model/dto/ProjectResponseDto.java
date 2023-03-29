package com.dp.gantt.persistence.model.dto;

import com.dp.gantt.persistence.model.GanttUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {

    private Long id;
    private String name;
    private String description;
    private GanttUser manager;
    private Integer resources;
    private List<GanttUser> members;
    private Instant startDate;
}
