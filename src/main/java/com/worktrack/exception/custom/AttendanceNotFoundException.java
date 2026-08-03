package com.worktrack.exception.custom;

public class AttendanceNotFoundException extends ResourceNotFoundException {

    public AttendanceNotFoundException(String message) {
        super(message);
    }

}