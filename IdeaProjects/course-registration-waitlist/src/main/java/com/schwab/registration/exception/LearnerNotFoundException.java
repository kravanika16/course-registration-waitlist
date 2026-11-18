package com.schwab.registration.exception;

public class LearnerNotFoundException extends RuntimeException {

    public LearnerNotFoundException(String message) {
        super(message);
    }
}
