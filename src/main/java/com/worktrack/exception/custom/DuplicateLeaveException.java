package com.worktrack.exception.custom;

public class DuplicateLeaveException extends DuplicateResourceException {

    public DuplicateLeaveException(String message) {
        super(message);
    }

}