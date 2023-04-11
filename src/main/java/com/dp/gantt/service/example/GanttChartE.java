package com.dp.gantt.service.example;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class GanttChartE {
    private List<TaskE> tasks = new ArrayList<>();

    public void addTask(TaskE task) {
        tasks.add(task);
    }

    public List<TaskE> getTasks() {
        return tasks;
    }

    public void calculateDates(Instant startDate) {
        Map<Integer, Integer> earliestStart = new HashMap<>();
        for (TaskE task : tasks) {
            earliestStart.put(task.getId(), 0);
        }

        for (TaskE task : tasks) {
            int maxDuration = 0;
            for (Predecessor pred : task.getPredecessors()) {
                int predFinish = earliestStart.get(pred.getId()) + findById(pred.getId()).getDuration();
                predFinish += findById(pred.getId()).getAddedGapDays();
                if (predFinish > maxDuration) {
                    maxDuration = predFinish;
                }
            }
            earliestStart.put(task.getId(), maxDuration);
            Instant newStartDate = startDate.plus(maxDuration, ChronoUnit.DAYS);
            Instant minStartDate = task.getMinimalStartDate();
            if(minStartDate != null){
                if(newStartDate.isBefore(minStartDate)){
                    maxDuration += (int) Duration.between(newStartDate, minStartDate).toDays();
                }
            }
            task.setStartDate(startDate.plus(maxDuration, ChronoUnit.DAYS));
            task.setEndDate(startDate.plus(maxDuration + task.getDuration() - 1, ChronoUnit.DAYS));
        }
    }

    public void editExtendableParallelTasks(){
        tasks.forEach(this::extendTasksDuration);
    }

    private static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    public void sortNonExtendableParallelTasks(){
        for (TaskE task: tasks){
            if (!task.isExtendable()) {

                for(TaskE t: tasks) {
                    if (t != task && !(t.getStartDate().isAfter(task.getEndDate()) || t.getEndDate().isBefore(task.getStartDate()))) {
                        List<Integer> sharedEmployees = getSharedEmployees(t, task);
                        if (!sharedEmployees.isEmpty() && !t.isExtendable()) {
                            if(listEqualsIgnoreOrder(task.getAssignees(), t.getAssignees())){
                                if(t.getPriority().ordinal() > task.getPriority().ordinal()){
                                    if(!getPredById(task.getPredecessors(), t.getId())) {
                                        Predecessor newPred = new Predecessor(t.getId(), true);
                                        task.addPredecessor(newPred);
                                    }
                                }
                                else{
                                    if(!getPredById(task.getPredecessors(), t.getId())) {
                                        Predecessor newPred = new Predecessor(task.getId(), true);
                                        t.addPredecessor(newPred);
                                    }
                                }
                            }
                            else {
                                Instant intersectionStart = task.getStartDate().isBefore(t.getStartDate()) ? t.getStartDate() : task.getStartDate();
                                Instant intersectionEnd = task.getEndDate().isBefore(t.getEndDate()) ? task.getEndDate() : t.getEndDate();
                                int intersectionDuration = (int) Duration.between(intersectionStart, intersectionEnd).toDays() + 1;

                                double sharedEmployeesWorkOnOtherTask = getSharedEmployeesDuration(t, sharedEmployees);
                                double nonSharedEmployeesWorkOnOtherTask = t.getDuration() - sharedEmployeesWorkOnOtherTask;

                                double sharedEmployeesWorkOnThisTask = getSharedEmployeesDuration(task, sharedEmployees);
                                double nonSharedEmployeesWorkOnThisTask = task.getDuration() - sharedEmployeesWorkOnThisTask;

                                if(intersectionDuration <= nonSharedEmployeesWorkOnOtherTask || intersectionDuration <= nonSharedEmployeesWorkOnThisTask){
                                    System.out.println("Leave it like this");
                                }
                                else {
                                    if(intersectionDuration < nonSharedEmployeesWorkOnOtherTask + nonSharedEmployeesWorkOnThisTask ||
                                    intersectionDuration > sharedEmployeesWorkOnOtherTask/sharedEmployees.size() + sharedEmployeesWorkOnThisTask/sharedEmployees.size()){
                                        System.out.println("Leave it like this");
                                    }
                                    else{
                                        if(task.getPriority().ordinal() > t.getPriority().ordinal()){
                                            Instant minimalStartDate = task.getEndDate().minus((int) Math.ceil(sharedEmployeesWorkOnThisTask) - 1, ChronoUnit.DAYS);
                                            Instant oldStartDate = t.getStartDate();
                                            t.setMinimalStartDate(minimalStartDate);
                                            int gap = (int) Duration.between(oldStartDate, minimalStartDate).toDays();
                                            t.setAddedGapDays(gap);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

    }

    private void extendTasksDuration(TaskE task) {
        // Check if task is extendable
        if (!task.isExtendable()) {
            return;
        }

        // Calculate additional days
        double additionalDays = 0.0;
        Map<Integer, Double> assigneesWeights = new HashMap<>();
        task.getAssignees().forEach(assignee -> assigneesWeights.put(assignee, 1.0));
        for (TaskE t : tasks) {
            // Check if task t is parallel and has shared employees
            if (t != task && !(t.getStartDate().isAfter(task.getEndDate()) || t.getEndDate().isBefore(task.getStartDate()))) {
                List<Integer> sharedEmployees = getSharedEmployees(t, task);
                if (!sharedEmployees.isEmpty()) {
                    if(listEqualsIgnoreOrder(task.getAssignees(), t.getAssignees())){
                        if(t.getPriority().ordinal() >= task.getPriority().ordinal()){
                            if(!getPredById(task.getPredecessors(), t.getId())) {
                                Predecessor newPred = new Predecessor(t.getId(), true);
                                task.addPredecessor(newPred);
                            }
                        }
                        else{
                            if(!getPredById(task.getPredecessors(), t.getId())) {
                                Predecessor newPred = new Predecessor(task.getId(), true);
                                t.addPredecessor(newPred);
                            }
                        }
                    }
                    else {
                        if (t.isExtendable()) {
                            increaseWeightOfAssignees(assigneesWeights, sharedEmployees);
                        } else {
                            double sharedEmployeesWork = getSharedEmployeesDuration(t, sharedEmployees);
                            double nonSharedEmployeesWork = t.getDuration() - sharedEmployeesWork;

                            Instant intersectionStart = task.getStartDate().isBefore(t.getStartDate()) ? t.getStartDate() : task.getStartDate();
                            Instant intersectionEnd = task.getEndDate().isBefore(t.getEndDate()) ? task.getEndDate() : t.getEndDate();
                            int intersectionDuration = (int) Duration.between(intersectionStart, intersectionEnd).toDays() + 1;
                            if (intersectionDuration > nonSharedEmployeesWork) {
                                Instant minimalStartDate = t.getEndDate().minus((int) Math.ceil(sharedEmployeesWork) - 1, ChronoUnit.DAYS);
                                Instant oldStartDate = t.getStartDate();
                                task.setMinimalStartDate(minimalStartDate);
                                int gap = (int) Duration.between(oldStartDate, minimalStartDate).toDays();
                                task.setAddedGapDays(gap);
                            }
                            //additionalDays += getSharedEmployeesDuration(t, sharedEmployees);
                        }
                    }
                }
            }
        }
        double newDuration = (task.getDuration()*task.getAssignees().size())/getSumOfWeights(assigneesWeights, task.getAssignees());
        double additionalTimeFromEPT = newDuration - task.getDuration();
        additionalDays += additionalTimeFromEPT;
        // Extend task duration
        task.setDuration(task.getDuration() + (int) Math.ceil(additionalDays));
        //task.setEndDate(task.getEndDate().plus((int) Math.ceil(additionalDays), ChronoUnit.DAYS));
    }

    private boolean getPredById(List<Predecessor> predecessors, Integer id){
        for(Predecessor predecessor: predecessors){
            if(predecessor.getId() == id){
                return true;
            }
        }
        return false;
    }

    private double getSumOfWeights(Map<Integer, Double> weights, List<Integer> assignees){
        double sum = 0;
        for(Integer assignee: assignees){
            sum += 1.0/weights.get(assignee);
        }
        return sum;
    }

    private void increaseWeightOfAssignees(Map<Integer, Double> assigneesWeights, List<Integer> sharedEmployees){
        for(Integer sharedEmployee: sharedEmployees){
            assigneesWeights.replace(sharedEmployee, assigneesWeights.get(sharedEmployee) + 1);
        }
    }

    public List<Integer> getSharedEmployees(TaskE t1, TaskE t2) {
        List<Integer> sharedEmployees = new ArrayList<>();
        for(Integer e1: t1.getAssignees()) {
            for(Integer e2: t2.getAssignees()) {
                if(e1.equals(e2)) {
                    sharedEmployees.add(e1);
                    break;
                }
            }
        }
        return sharedEmployees;
    }

    public int getNumberOfParallelTasksWithEmployees(TaskE t, List<Integer> sharedEmployees) {
        int count = 0;
        for(TaskE task: tasks) {
            if(task.getId() != t.getId()) {
                List<Integer> temp = getSharedEmployees(t, task);
                if(temp.size() > 0) {
                    boolean allShared = true;
                    for(Integer e: sharedEmployees) {
                        if(!temp.contains(e)) {
                            allShared = false;
                            break;
                        }
                    }
                    if(allShared) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public double getSharedEmployeesDuration(TaskE t, List<Integer> sharedEmployees) {
        return sharedEmployees.size()*(t.getDuration()/t.getAssignees().size());
    }


    public String generateGanttChart() {
        StringBuilder sb = new StringBuilder();
        sb.append("+------------+------------+--------------+\n");
        sb.append("|    Task    |  Start     |     End      |\n");
        sb.append("+------------+------------+--------------+\n");
        for (TaskE task : tasks) {
            sb.append(String.format("| %10d | %10s | %12s |\n", task.getId(), task.getStartDate(), task.getEndDate()));
        }
        sb.append("+------------+------------+--------------+\n");
        return sb.toString();
    }

    private TaskE findById(int id) {
        for (TaskE task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }
}

