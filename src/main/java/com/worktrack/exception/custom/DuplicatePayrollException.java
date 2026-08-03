package com.worktrack.exception.custom;

public class DuplicatePayrollException extends RuntimeException {

    public DuplicatePayrollException(String message) {
        super(message);
    }

}