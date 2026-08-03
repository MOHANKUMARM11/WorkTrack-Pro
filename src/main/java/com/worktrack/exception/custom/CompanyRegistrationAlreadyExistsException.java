package com.worktrack.exception.custom;

public class CompanyRegistrationAlreadyExistsException extends DuplicateResourceException {

    public CompanyRegistrationAlreadyExistsException(String message) {
        super(message);
    }

}