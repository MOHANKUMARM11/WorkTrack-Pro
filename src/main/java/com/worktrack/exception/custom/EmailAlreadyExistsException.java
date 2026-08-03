package com.worktrack.exception.custom;

public class EmailAlreadyExistsException extends DuplicateResourceException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

}