package com.worktrack.exception.custom;

public class EmployeeNotFoundException extends ResourceNotFoundException {

    public EmployeeNotFoundException(String message) {
        super(message);
    }

}