package com.worktrack.exception.custom;

public class TaskNotFoundException extends ResourceNotFoundException {

    public TaskNotFoundException(String message) {
        super(message);
    }

}