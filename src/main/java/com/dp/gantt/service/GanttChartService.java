package com.dp.gantt.service;

import com.dp.gantt.exceptions.GanttChartIsCyclicException;
import com.dp.gantt.exceptions.PhaseNotFoundException;
import com.dp.gantt.exceptions.TaskNotFoundException;
import com.dp.gantt.persistence.model.GanttChart;
import com.dp.gantt.persistence.model.Phase;
import com.dp.gantt.persistence.model.Project;
import com.dp.gantt.persistence.model.Task;
import com.dp.gantt.persistence.model.dto.GanttChartDto;
import com.dp.gantt.persistence.model.dto.PhaseDto;
import com.dp.gantt.persistence.model.dto.TaskDto;
import com.dp.gantt.persistence.repository.GanttChartRepository;
import com.dp.gantt.persistence.repository.PhaseRepository;
import com.dp.gantt.persistence.repository.ProjectRepository;
import com.dp.gantt.persistence.repository.TaskRepository;
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
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class GanttChartService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    private GanttChartRepository ganttChartRepository;

    private TaskRepository taskRepository;

    private PhaseRepository phaseRepository;

    private ProjectRepository projectRepository;

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

        return ganttChartGenerator.generateGanttChartResult();
    }

    public void addGanttChartToProject(GanttChartDto ganttChartDto){
        System.out.println(ganttChartDto);
        long projectId = ganttChartDto.getProject();
        Project project = projectService.findProject(projectId);
        GanttChart ganttChart = new GanttChart();
        ganttChart.setProject(project);
        GanttChart savedGantt = ganttChartRepository.save(ganttChart);
        project.setGanttChart(savedGantt);
        projectRepository.save(project);
        List<PhaseDto> projectPhases = ganttChartDto.getPhases();
        savePhases(projectPhases, savedGantt);
        saveProjectTasks(projectPhases);
        addDependenciesToTasks(projectPhases, savedGantt.getId());
    }

    public Phase getPhase(Optional<Long> id){
        return phaseRepository.findById(id.orElse(-1L))
                .orElseThrow(() -> {
                    log.error("Task with id = {} can not be find while getting task", id);
                    throw new PhaseNotFoundException(id.orElse(-1L));
                });
    }

    private void savePhases(List<PhaseDto> phases, GanttChart ganttChart){
        phases.forEach(phaseDto -> {
            Phase newPhase = new Phase();
            newPhase.setName(phaseDto.getName());
            newPhase.setGanttChart(ganttChart);
            Phase savedPhase = phaseRepository.save(newPhase);
            phaseDto.setRealId(Optional.of(savedPhase.getId()));
        });
    }

    private void saveProjectTasks(List<PhaseDto> phases){
        for (PhaseDto phase: phases){
            List<TaskDto> tasks = phase.getTasks();
            for( TaskDto taskDto: tasks) {
                Task newTask = new Task();
                newTask.setWorkId(taskDto.getWorkId());
                newTask.setName(taskDto.getName());
                newTask.setPriority(taskDto.getPriority());
                newTask.setDuration(taskDto.getDuration());
                newTask.setResources(taskDto.getResources());
                newTask.setExtendable(taskDto.getExtendable());
                newTask.setStartDate(taskDto.getStartDate());
                newTask.setEndDate(taskDto.getEndDate());
                newTask.setPhase(getPhase(phase.getRealId()));
                Task savedTask = taskRepository.save(newTask);
                taskDto.setRealId(Optional.of(savedTask.getId()));
            }
        }
    }

    private void addDependenciesToTasks(List<PhaseDto> phases, Long ganttId){
        for (PhaseDto phase: phases){
            List<TaskDto> tasks = phase.getTasks();
            for( TaskDto taskDto: tasks) {
                Task updatedTask = taskService.updateDependenciesInTask(taskDto, taskDto.getRealId(), ganttId);
                taskRepository.save(updatedTask);
            }
        }
    }
}
