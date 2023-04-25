package com.dp.gantt.controller;

import com.dp.gantt.model.GanttChartInfo;
import com.dp.gantt.persistence.model.dto.GanttChartDto;
import com.dp.gantt.persistence.model.dto.GanttRequestDto;
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

    @PostMapping("/create")
    public GanttChartDto createGanttChart(@Valid @RequestBody GanttRequestDto ganttRequestDto){
        return ganttChartService.generateGanttChart(ganttRequestDto.getPhases(), ganttRequestDto.getProjectId());
    }

    @PostMapping("/save")
    public void saveGanttChart(@Valid @RequestBody GanttChartDto ganttChart){
        ganttChartService.addGanttChartToProject(ganttChart);
    }

    @PostMapping("/edit")
    public void editGanttChart(@Valid @RequestBody GanttChartDto ganttChart){
        ganttChartService.updateGanttChart(ganttChart);
    }

    @GetMapping
    public GanttChartDto getProjectGanttChart(@RequestParam Long id){
        return ganttChartService.getGanttChartByProjectId(id);
    }
}
