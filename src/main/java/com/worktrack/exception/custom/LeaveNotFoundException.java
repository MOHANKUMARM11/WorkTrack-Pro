package com.worktrack.exception.custom;

public class LeaveNotFoundException extends ResourceNotFoundException {

    public LeaveNotFoundException(String message) {
        super(message);
    }

}