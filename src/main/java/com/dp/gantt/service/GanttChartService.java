package com.dp.gantt.service;

import com.dp.gantt.exceptions.GanttChartIsCyclicException;
import com.dp.gantt.exceptions.GanttUserNotFoundException;
import com.dp.gantt.exceptions.PhaseNotFoundException;
import com.dp.gantt.exceptions.TaskNotFoundException;
import com.dp.gantt.model.GanttChartInfo;
import com.dp.gantt.persistence.model.*;
import com.dp.gantt.persistence.model.dto.GanttChartDto;
import com.dp.gantt.persistence.model.dto.PhaseDto;
import com.dp.gantt.persistence.model.dto.TaskDto;
import com.dp.gantt.persistence.repository.*;
import com.dp.gantt.service.ganttChartGenerator.GanttChartGenerator;
import com.dp.gantt.service.ganttChartGenerator.Predecessor;
import com.dp.gantt.service.ganttChartGenerator.TaskE;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

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

    private GanttUserRepository ganttUserRepository;

    public GanttChartDto generateGanttChart(List<PhaseDto> phases, Long projectId){
        long start1 = System.nanoTime();
        Instant projectStartDate = projectService.findProject(projectId).getStartDate();
        GanttChartGenerator ganttChartGenerator = new GanttChartGenerator(projectStartDate, projectId);

        Long helpId = 1000L;
        List<TaskE> helpTasks = new ArrayList<>();
        for(PhaseDto phase: phases){
            List<Predecessor> predecessors= new ArrayList<>();
            phase.getTasks().forEach(taskDto -> {
                Predecessor newPred = new Predecessor(taskDto.getWorkId(), true);
                predecessors.add(newPred);
            });
            TaskE helpTask = new TaskE(
                    helpId,
                    0,
                    "help task",
                    predecessors,
                    List.of(),
                    TaskPriority.LOW,
                    phase.getWorkId(),
                    phase.getName(),
                    0,
                    0,
                    true);
            helpTasks.add(helpTask);
            helpId++;
        }

        PhaseDto lastPhase = null;
        for(PhaseDto phase: phases){
            for(TaskDto taskDto: phase.getTasks()) {
                List<Predecessor> predecessors = new ArrayList<>();
                taskDto.getPredecessors().forEach(predecessor -> {
                    predecessors.add(new Predecessor(predecessor, false));
                });
                if(lastPhase != null) {
                    PhaseDto finalLastPhase = lastPhase;
                    TaskE helpTask = helpTasks.stream()
                            .filter(taskE -> taskE.getPhaseInfo().getId() == finalLastPhase.getWorkId())
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Help task not found"));
                    Predecessor helpPred = new Predecessor(helpTask.getId(), true);
                    predecessors.add(helpPred);
                }

                TaskE taskE = new TaskE(
                        taskDto.getWorkId(),
                        taskDto.getDuration(),
                        taskDto.getName(),
                        predecessors,
                        taskDto.getAssignees(),
                        taskDto.getPriority(),
                        phase.getWorkId(),
                        phase.getName(),
                        taskDto.getResources(),
                        taskDto.getState(),
                        false
                );
                ganttChartGenerator.addTask(taskE);
            }
            ganttChartGenerator.addTask(helpTasks
                    .stream()
                    .filter(taskE -> taskE.getPhaseInfo().getId() == phase.getWorkId())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Help task not found"))
            );
            lastPhase = phase;
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

        GanttChartDto result = ganttChartGenerator.generateGanttChartResult();
        long end1 = System.nanoTime();
        System.out.println("Elapsed Time in nano seconds: "+ (end1-start1));
        return result;
    }

    public GanttChartDto getGanttChartByProjectId(Long id){
        List<PhaseDto> phases = new ArrayList<>();
        GanttChart ganttChart = projectService.findProject(id).getGanttChart();

        List<Phase> ganttPhases = phaseRepository.findAllByGanttChart_Id(ganttChart.getId());
        ganttPhases.forEach(phase -> {
            PhaseDto newPhase = new PhaseDto(phase.getId(), phase.getName(), id);
            newPhase.setRealId(Optional.of(phase.getId()));
            List<Task> tasks = taskRepository.findAllByPhase_IdAndPhase_GanttChart_Id(phase.getId(), ganttChart.getId());
            tasks.forEach(task -> {
                TaskDto taskDto = new TaskDto(
                        task.getWorkId(),
                        task.getName(),
                        task.getPriority(),
                        task.getDuration(),
                        task.getResources(),
                        task.getPredecessors().stream().map(Task::getWorkId).toList(),
                        task.getAssignees().stream().map(GanttUser::getId).toList(),
                        task.getStartDate(),
                        task.getEndDate(),
                        task.getState()
                );
                taskDto.setRealId(Optional.of(task.getId()));
                newPhase.addTask(taskDto);
            });
            phases.add(newPhase);
        });

        return new GanttChartDto(ganttChart.getId(), phases, id);
    }

    public GanttChartDto addGanttChartToProject(GanttChartDto ganttChartDto){
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
        return getGanttChartByProjectId(projectId);
    }

    public void updateGanttChart(GanttChartDto ganttChartDto){
        long projectId = ganttChartDto.getProject();
        GanttChart ganttChart = projectService.findProject(projectId).getGanttChart();
        List<Phase> ganttPhases = phaseRepository.findAllByGanttChart_Id(ganttChart.getId());

        for(Phase phase: ganttPhases){
            List<Task> phaseTasks = taskRepository.findAllByPhase_IdAndPhase_GanttChart_Id(phase.getId(), ganttChart.getId());
            PhaseDto updatedPhase = null;
            for(PhaseDto phaseDto: ganttChartDto.getPhases()) {
                Long phaseDtoId = phaseDto.getRealId().orElseThrow(() -> new PhaseNotFoundException(phase.getId()));
                if (Objects.equals(phaseDtoId, phase.getId())) {
                    updatedPhase = phaseDto;
                }
            }
            if(updatedPhase != null){
                for(Task task: phaseTasks){
                    TaskDto updatedPhaseTask = updatedPhase.getTasks().stream().filter((taskDto -> {
                        Long taskDtoId = taskDto.getRealId().orElseThrow(() -> new TaskNotFoundException(task.getId()));
                        return Objects.equals(taskDtoId, task.getId());
                    })).toList().get(0);
                    task.setDuration(updatedPhaseTask.getDuration());
                    task.setState(updatedPhaseTask.getState());
                    task.setResources(updatedPhaseTask.getResources());
                    task.setPriority(updatedPhaseTask.getPriority());
                    task.setStartDate(updatedPhaseTask.getStartDate());
                    task.setEndDate(updatedPhaseTask.getEndDate());
                    List<GanttUser> assignees = new ArrayList<>();
                    for(Long assigneeId: updatedPhaseTask.getAssignees()){
                        GanttUser newAssignee = ganttUserRepository.findById(assigneeId).orElseThrow(() -> new GanttUserNotFoundException(assigneeId));
                        assignees.add(newAssignee);
                    }
                    task.setAssignees(assignees);
                    taskRepository.save(task);
                }
            }
        }

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
                newTask.setStartDate(taskDto.getStartDate());
                newTask.setEndDate(taskDto.getEndDate());
                newTask.setPhase(getPhase(phase.getRealId()));
                newTask.setState(taskDto.getState());
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
