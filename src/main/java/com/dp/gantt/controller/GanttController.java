package com.dp.gantt.controller;

import com.dp.gantt.persistence.model.GanttChart;
import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.dto.GanttChartDto;
import com.dp.gantt.persistence.model.dto.GanttRequestDto;
import com.dp.gantt.persistence.model.dto.ProjectRequestDto;
import com.dp.gantt.service.GanttChartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ganttChart")
public class GanttController {

    @Autowired
    private GanttChartService ganttChartService;

    @PostMapping()
    public GanttChartDto createGanttChart(@Valid @RequestBody GanttRequestDto ganttRequestDto){
        return ganttChartService.generateGanttChart(ganttRequestDto.getPhases(), ganttRequestDto.getProjectId());
    }
}
