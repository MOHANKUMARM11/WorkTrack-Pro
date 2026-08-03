package com.worktrack.exception.custom;

public class DuplicateAttendanceException extends DuplicateResourceException {

    public DuplicateAttendanceException(String message) {
        super(message);
    }

}