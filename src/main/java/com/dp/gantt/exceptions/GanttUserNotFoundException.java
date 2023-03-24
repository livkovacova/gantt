package com.dp.gantt.exceptions;

public class GanttUserNotFoundException extends RuntimeException{
    public GanttUserNotFoundException(Long id){
        super("Team member with id = " + id + " was not found.");
    }
}
