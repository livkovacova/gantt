package com.dp.gantt.service;

import com.dp.gantt.exceptions.GanttChartIsCyclicException;
import com.dp.gantt.persistence.model.GanttChart;
import com.dp.gantt.persistence.model.dto.GanttChartDto;
import com.dp.gantt.persistence.model.dto.PhaseDto;
import com.dp.gantt.service.ganttChartGenerator.GanttChartGenerator;
import com.dp.gantt.service.ganttChartGenerator.Predecessor;
import com.dp.gantt.service.ganttChartGenerator.TaskE;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GanttChartService {

    @Autowired
    private ProjectService projectService;

    public GanttChartDto generateGanttChart(List<PhaseDto> phases, Long projectId){
        Instant projectStartDate = projectService.findProject(projectId).getStartDate();
        GanttChartGenerator ganttChartGenerator = new GanttChartGenerator(projectStartDate, projectId);

        for(PhaseDto phase: phases){
            phase.getTasks().forEach(taskDto -> {
                List<Predecessor> predecessors = new ArrayList<>();
                taskDto.getPredecessors().forEach(predecessor -> {
                    predecessors.add(new Predecessor(predecessor, false));
                });

                TaskE taskE = new TaskE(
                        taskDto.getWorkId(),
                        taskDto.getDuration(),
                        taskDto.getName(),
                        predecessors,
                        taskDto.getAssignees(),
                        taskDto.getPriority(),
                        taskDto.getExtendable(),
                        phase.getWorkId(),
                        phase.getName(),
                        taskDto.getResources()
                );

                ganttChartGenerator.addTask(taskE);
            });
        }

        if(ganttChartGenerator.isGraphCyclic()){
            log.error("Tasks predecessors settings created a cyclic dependencies.");
            throw new GanttChartIsCyclicException();
        }

        ganttChartGenerator.computeMinimalDurations();
        ganttChartGenerator.getTasks().sort(Comparator.comparing((TaskE task) -> task.getPriority().ordinal()).reversed());
        ganttChartGenerator.calculateDates();
        ganttChartGenerator.sortNonExtendableParallelTasks();
        ganttChartGenerator.calculateDates();

        //uloz ganttchart do db, nastav projektu ganttchart, nastav ye gantt chart je v projekte spraveny
        return ganttChartGenerator.generateGanttChartResult();
    }
}
