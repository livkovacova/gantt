package com.dp.gantt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class GanttChartIsCyclicException extends RuntimeException {

    public GanttChartIsCyclicException(){
        super("Tasks predecessors settings created a cyclic dependencies.");
    }
}
