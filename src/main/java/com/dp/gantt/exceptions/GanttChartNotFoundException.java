package com.dp.gantt.exceptions;

public class GanttChartNotFoundException extends RuntimeException{
    public GanttChartNotFoundException(Long id){
        super("Task with id = " + id + " was not found.");
    }
}

