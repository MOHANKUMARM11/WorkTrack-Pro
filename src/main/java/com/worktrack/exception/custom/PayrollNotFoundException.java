package com.worktrack.exception.custom;

public class PayrollNotFoundException extends ResourceNotFoundException {

    public PayrollNotFoundException(String message) {
        super(message);
    }

}