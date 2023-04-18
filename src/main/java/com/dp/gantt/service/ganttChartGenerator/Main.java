package com.dp.gantt.service.ganttChartGenerator;

import com.dp.gantt.persistence.model.TaskPriority;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Instant startDate = Instant.parse("2020-01-01T00:00:00.00Z");
        GanttChartGenerator ganttChart = new GanttChartGenerator(startDate, 1L);

        List<Predecessor> datalist = new ArrayList<>();
        datalist.add(new Predecessor(1L, false));
        datalist.add(new Predecessor(2L, false));
        List<Predecessor> dokulist1 = new ArrayList<>();
        dokulist1.add(new Predecessor(3L, false));
        List<Predecessor> dokulist2 = new ArrayList<>();
        dokulist2.add(new Predecessor(3L, false));
//        List<Predecessor> list5 = new ArrayList<>();
//        list5.add(new Predecessor(4, false));
//        List<Predecessor> list6 = new ArrayList<>();
//        list6.add(new Predecessor(5, false));
//        List<Predecessor> list7 = new ArrayList<>();
//        list7.add(new Predecessor(2, false));
//        TaskE task1 = new TaskE(1, 4, new ArrayList<>(), List.of(21), Priority.LOW, false);
//        TaskE task2 = new TaskE(2, 6, new ArrayList<>(), List.of(21,22), Priority.HIGH, false);
//        TaskE task3 = new TaskE(3, 4, list3, List.of(21), Priority.LOW, true);
//        TaskE task4 = new TaskE(4, 3, list4, List.of(21,22,23), Priority.LOW, false);
//        TaskE task5 = new TaskE(5, 3, list5, List.of(21,22,23), Priority.LOW, true);
//        TaskE task6 = new TaskE(6, 3, list6, List.of(21,22,23), Priority.HIGH, false);

        TaskE analysis = new TaskE(1L, 5, "A", new ArrayList<>(), List.of(1L, 2L, 3L), TaskPriority.MEDIUM, 1L, "phase", 3);
        TaskE analysis2 = new TaskE(2L, 4, "B", new ArrayList<>(), List.of(1L, 2L), TaskPriority.MEDIUM, 1L, "phase", 3);
        TaskE data = new TaskE(3L, 5,  "C", datalist, List.of(3L, 4L), TaskPriority.HIGH, 1L, "phase", 3);
        TaskE doku1 = new TaskE(4L, 3,  "D", dokulist1, List.of(1L, 2L), TaskPriority.HIGH, 1L, "phase", 3);
        TaskE doku2 = new TaskE(5L, 5, "E", dokulist2,  List.of(1L, 3L), TaskPriority.HIGH, 1L, "phase", 3);

        ganttChart.addTask(analysis);
        ganttChart.addTask(analysis2);
        ganttChart.addTask(data);
        ganttChart.addTask(doku1);
        ganttChart.addTask(doku2);


        if(ganttChart.isGraphCyclic()){
            System.out.println("cyclic  graph");
            return;
        }

        System.out.println(ganttChart.computeMinimalDurations());

        ganttChart.getTasks().sort(Comparator.comparing((TaskE task) -> task.getPriority().ordinal()).reversed());
        ganttChart.calculateDates();
        System.out.println(ganttChart.generateGanttChart());
        ganttChart.sortNonExtendableParallelTasks();
        ganttChart.calculateDates();
        System.out.println(ganttChart.generateGanttChart());
//        ganttChart.extendTasksDuration();
//        ganttChart.calculateDates(startDate);
//        System.out.println(ganttChart.generateGanttChart());

    }
}

