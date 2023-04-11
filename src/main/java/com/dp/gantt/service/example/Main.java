package com.dp.gantt.service.example;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GanttChartE ganttChart = new GanttChartE();

        Instant startDate = Instant.parse("2020-01-01T00:00:00.00Z");

        List<Predecessor> listW = new ArrayList<>();
        listW.add(new Predecessor(2, false));
        TaskE task1 = new TaskE(1, 6, new ArrayList<>(), List.of(21,22), Priority.LOW, false);
        TaskE task2 = new TaskE(2, 4, new ArrayList<>(), List.of(21), Priority.HIGH, false);
        ganttChart.addTask(task1);
        ganttChart.addTask(task2);

        ganttChart.calculateDates(startDate);
        System.out.println(ganttChart.generateGanttChart());
        ganttChart.sortNonExtendableParallelTasks();
        ganttChart.calculateDates(startDate);
        System.out.println(ganttChart.generateGanttChart());
        ganttChart.editExtendableParallelTasks();
        ganttChart.calculateDates(startDate);
        System.out.println(ganttChart.generateGanttChart());

    }
}

