package com.dp.gantt.exceptions;

public class PhaseNotFoundException extends RuntimeException{
    public PhaseNotFoundException(Long id){
        super("Phase with id = " + id + " was not found.");
    }
}
