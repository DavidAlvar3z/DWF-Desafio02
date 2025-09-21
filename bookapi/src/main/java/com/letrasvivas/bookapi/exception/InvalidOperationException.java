package com.letrasvivas.bookapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidOperationException extends RuntimeException {

    private String operation;
    private String currentState;

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, String operation, String currentState) {
        super(message);
        this.operation = operation;
        this.currentState = currentState;
    }

    public String getOperation() {
        return operation;
    }

    public String getCurrentState() {
        return currentState;
    }
}