package com.worktrack.exception.custom;

public class CompanyNameAlreadyExistsException extends DuplicateResourceException {

    public CompanyNameAlreadyExistsException(String message) {
        super(message);
    }

}