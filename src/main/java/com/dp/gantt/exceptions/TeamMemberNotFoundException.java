package com.dp.gantt.exceptions;

public class TeamMemberNotFoundException extends RuntimeException{
    public TeamMemberNotFoundException(Long id){
        super("Team member with id = " + id + " was not found.");
    }
}
