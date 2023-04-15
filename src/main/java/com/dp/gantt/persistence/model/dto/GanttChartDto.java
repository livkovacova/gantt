package com.dp.gantt.persistence.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GanttChartDto {

    private Long id;

    private List<PhaseDto> phases;

    private Long project;
}
