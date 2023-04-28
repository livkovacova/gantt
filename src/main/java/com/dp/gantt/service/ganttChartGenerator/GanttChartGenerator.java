package com.dp.gantt.service.ganttChartGenerator;

import com.dp.gantt.persistence.model.GanttChart;
import com.dp.gantt.persistence.model.Phase;
import com.dp.gantt.persistence.model.dto.GanttChartDto;
import com.dp.gantt.persistence.model.dto.PhaseDto;
import com.dp.gantt.persistence.model.dto.TaskDto;
import com.dp.gantt.service.TaskService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class GanttChartGenerator {
    private List<TaskE> tasks = new ArrayList<>();

    public void addTask(TaskE task) {
        tasks.add(task);
    }

    public List<TaskE> getTasks() {
        return tasks;
    }

    private Instant startDate;

    private Long projectId;

    public GanttChartGenerator(Instant startDate, Long projectId) {
        this.startDate = startDate;
        this.projectId = projectId;
    }

    public boolean isGraphCyclic() {
        Map<Long, List<Long>> graph = buildGraph(tasks);
        Set<Long> visited = new HashSet<>();
        Set<Long> recStack = new HashSet<>();

        for (Long task : graph.keySet()) {
            if (isCyclicUtil(task, visited, recStack, graph)) {
                return true;
            }
        }

        return false;
    }

    // Build a directed graph from the task list
    private static Map<Long, List<Long>> buildGraph(List<TaskE> tasks) {
        Map<Long, List<Long>> graph = new HashMap<>();
        for (TaskE task : tasks) {
            List<Long> predecessors = new ArrayList<>();
            task.getPredecessors().forEach(pred -> {
                predecessors.add(pred.getId());
            });
            System.out.println("id" + task.getId());
            graph.put(task.getId(), predecessors);
        }
        return graph;
    }

    // Utility function to check if the graph is cyclic
    private static boolean isCyclicUtil(Long task, Set<Long> visited, Set<Long> recStack, Map<Long, List<Long>> graph) {
        if (recStack.contains(task)) {
            return true;
        }

        if (visited.contains(task)) {
            return false;
        }

        visited.add(task);
        recStack.add(task);

        List<Long> neighbors = graph.get(task);
        if (neighbors != null) {
            for (long neighbor : neighbors) {
                if (isCyclicUtil(neighbor, visited, recStack, graph)) {
                    return true;
                }
            }
        }

        recStack.remove(task);

        return false;
    }

    public Map<Long, Integer> computeMinimalDurations() {
        Map<Long, List<Long>> graph = buildGraph(tasks);
        Map<Long, Integer> durations = new HashMap<>();

        System.out.println(tasks);
        System.out.println(graph);
        System.out.println(graph.keySet());
        // Calculate the minimal duration for each task
        for (long task : graph.keySet()) {
            computeMinimalDurationUtil(task, graph, durations);
        }

        return durations;
    }

    // Utility function to compute the minimal duration for a task
    private int computeMinimalDurationUtil(long task, Map<Long, List<Long>> graph, Map<Long, Integer> durations) {
        if (durations.containsKey(task)) {
            return durations.get(task);
        }

        List<Long> predecessors = graph.get(task);
        int maxDuration = 0;
        if (predecessors != null) {
            for (long predecessor : predecessors) {
                int duration = computeMinimalDurationUtil(predecessor, graph, durations);
                maxDuration = Math.max(maxDuration, duration);
            }
        }

        int minimalDuration = maxDuration + findById(task).getDuration() + findById(task).getAddedGapDays();
        durations.put(task, minimalDuration);

        return minimalDuration;
    }


    public void calculateDates() {
        Map<Long, Integer> earliestStart = new HashMap<>();
        for (TaskE task : tasks) {
            earliestStart.put(task.getId(), 0);
        }
        Map<Long, Integer> minimalDurations = computeMinimalDurations();
        System.out.println(minimalDurations);
        for (TaskE task : tasks) {
            int maxDuration = minimalDurations.get(task.getId()) - task.getDuration();
            earliestStart.put(task.getId(), maxDuration);
            Instant newStartDate = startDate.plus(maxDuration, ChronoUnit.DAYS);
            Instant minStartDate = task.getMinimalStartDate();
            if (minStartDate != null) {
                if (newStartDate.isBefore(minStartDate)) {
                    maxDuration += (int) Duration.between(newStartDate, minStartDate).toDays();
                }
            }
            task.setStartDate(startDate.plus(maxDuration, ChronoUnit.DAYS));
            task.setEndDate(startDate.plus(maxDuration + task.getDuration() - 1, ChronoUnit.DAYS));
        }
    }

    private static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    public void sortNonExtendableParallelTasks() {
        boolean change = false;
        for (TaskE task : tasks) {
            for (TaskE t : tasks) {
                if (t.getId() != task.getId() && !(t.getStartDate().isAfter(task.getEndDate()) || t.getEndDate().isBefore(task.getStartDate()))) {
                    List<Long> sharedEmployees = getSharedEmployees(t, task);
                    if (!sharedEmployees.isEmpty()) {
                        if (listEqualsIgnoreOrder(task.getAssignees(), t.getAssignees())) {
                            if (t.getPriority().ordinal() > task.getPriority().ordinal()) {
                                if (!getPredById(task.getPredecessors(), t.getId())) {
                                    Predecessor newPred = new Predecessor(t.getId(), true);
                                    task.addPredecessor(newPred);
                                    System.out.println("Prva vetva Added pred like this " + task.getId() + " " + t.getId());
                                    change = true;
                                }
                            } else {
                                if (!getPredById(t.getPredecessors(), task.getId())) {
                                    Predecessor newPred = new Predecessor(task.getId(), true);
                                    t.addPredecessor(newPred);
                                    System.out.println("Druha vetva Added pred like this " + t.getId() + " " + task.getId());
                                    change = true;
                                }
                            }
                        } else {
                            Instant intersectionStart = task.getStartDate().isBefore(t.getStartDate()) ? t.getStartDate() : task.getStartDate();
                            Instant intersectionEnd = task.getEndDate().isBefore(t.getEndDate()) ? task.getEndDate() : t.getEndDate();
                            int intersectionDuration = (int) Duration.between(intersectionStart, intersectionEnd).toDays() + 1;

                            double sharedEmployeesWorkOnOtherTask = getSharedEmployeesDuration(t, sharedEmployees);
                            double nonSharedEmployeesWorkOnOtherTask = t.getDuration() - sharedEmployeesWorkOnOtherTask;

                            double sharedEmployeesWorkOnThisTask = getSharedEmployeesDuration(task, sharedEmployees);
                            double nonSharedEmployeesWorkOnThisTask = task.getDuration() - sharedEmployeesWorkOnThisTask;

                            if (intersectionDuration <= nonSharedEmployeesWorkOnOtherTask || intersectionDuration <= nonSharedEmployeesWorkOnThisTask) {
                                System.out.println("Leave it like this");
                            } else {
                                if (intersectionDuration < nonSharedEmployeesWorkOnOtherTask + nonSharedEmployeesWorkOnThisTask ||
                                        intersectionDuration > sharedEmployeesWorkOnOtherTask / sharedEmployees.size() + sharedEmployeesWorkOnThisTask / sharedEmployees.size()) {
                                    System.out.println("Leave it like this");
                                } else {
                                    if (task.getPriority().ordinal() >= t.getPriority().ordinal()) {
                                        Instant minimalStartDate = task.getEndDate().minus((int) Math.ceil(nonSharedEmployeesWorkOnThisTask) - 1, ChronoUnit.DAYS);
                                        Instant oldStartDate = t.getStartDate();
                                        t.setMinimalStartDate(minimalStartDate);
                                        t.setStartDate(minimalStartDate);
                                        t.setEndDate(minimalStartDate.plus(t.getDuration() - 1, ChronoUnit.DAYS));
                                        int gap = (int) Duration.between(oldStartDate, minimalStartDate).toDays();
                                        t.setAddedGapDays(gap);
                                        System.out.println("Task postponed like this " + t.getId() + " " + t.getStartDate());
                                        change = true;
                                    }
                                }
                            }
                        }
                        if (change) {
                            calculateDates();
                            sortNonExtendableParallelTasks();
                        }
                    }
                }
            }
        }

    }

    private boolean getPredById(List<Predecessor> predecessors, Long id) {
        for (Predecessor predecessor : predecessors) {
            if (predecessor.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private double getSumOfWeights(Map<Integer, Double> weights, List<Integer> assignees) {
        double sum = 0;
        for (Integer assignee : assignees) {
            sum += 1.0 / weights.get(assignee);
        }
        return sum;
    }

    private void increaseWeightOfAssignees(Map<Integer, Double> assigneesWeights, List<Integer> sharedEmployees) {
        for (Integer sharedEmployee : sharedEmployees) {
            assigneesWeights.replace(sharedEmployee, assigneesWeights.get(sharedEmployee) + 1);
        }
    }

    public List<Long> getSharedEmployees(TaskE t1, TaskE t2) {
        List<Long> sharedEmployees = new ArrayList<>();
        for (Long e1 : t1.getAssignees()) {
            for (Long e2 : t2.getAssignees()) {
                if (e1.equals(e2)) {
                    sharedEmployees.add(e1);
                    break;
                }
            }
        }
        return sharedEmployees;
    }

    public int getNumberOfParallelTasksWithEmployees(TaskE t, List<Integer> sharedEmployees) {
        int count = 0;
        for (TaskE task : tasks) {
            if (task.getId() != t.getId()) {
                List<Long> temp = getSharedEmployees(t, task);
                if (temp.size() > 0) {
                    boolean allShared = true;
                    for (Integer e : sharedEmployees) {
                        if (!temp.contains(e)) {
                            allShared = false;
                            break;
                        }
                    }
                    if (allShared) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public double getSharedEmployeesDuration(TaskE t, List<Long> sharedEmployees) {
        return sharedEmployees.size() * ((double) t.getDuration() / t.getAssignees().size());
    }

    public GanttChartDto generateGanttChartResult() {
        List<PhaseDto> phases = new ArrayList<>();
        Long currentPhase = -1L;

        for (TaskE taskE : tasks) {
            if(!taskE.isHelpTask()) {
                if (currentPhase != taskE.getPhaseInfo().getId()) {
                    phases.add(new PhaseDto(taskE.getPhaseInfo().getId(), taskE.getPhaseInfo().getName(), projectId));
                    currentPhase = taskE.getPhaseInfo().getId();
                }
                List<Long> predecessors = taskE.getPredecessors().stream()
                        .filter(predecessor -> !predecessor.isHidden())
                        .map(Predecessor::getId)
                        .toList();

                TaskDto taskDto = new TaskDto(
                        taskE.getId(),
                        taskE.getName(),
                        taskE.getPriority(),
                        taskE.getDuration(),
                        taskE.getResources(),
                        predecessors,
                        taskE.getAssignees(),
                        taskE.getStartDate(),
                        taskE.getEndDate(),
                        taskE.getState()
                );
                System.out.println("state in result is: " + taskE.getState());
                PhaseDto phase = findPhaseById(taskE.getPhaseInfo().getId(), phases);
                phase.addTask(taskDto);
            }
        }

        return new GanttChartDto(null, phases, projectId);
    }

    private PhaseDto findPhaseById(Long id, List<PhaseDto> phases) {
        PhaseDto toReturn = null;
        for (PhaseDto phaseDto : phases) {
            if (phaseDto.getWorkId() == id) {
                toReturn = phaseDto;
            }
        }
        return toReturn;
    }

    private TaskE findById(long id) {
        for (TaskE task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }
}

