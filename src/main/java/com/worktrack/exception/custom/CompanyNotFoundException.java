package com.worktrack.exception.custom;

public class CompanyNotFoundException extends ResourceNotFoundException {

    public CompanyNotFoundException(String message) {
        super(message);
    }

}