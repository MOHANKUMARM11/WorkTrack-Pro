package com.worktrack.exception.custom;

public class TaskTitleAlreadyExistsException extends DuplicateResourceException {

    public TaskTitleAlreadyExistsException(String message) {
        super(message);
    }

}