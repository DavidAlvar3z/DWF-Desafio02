package com.letrasvivas.bookapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseOperationException extends RuntimeException {

    private String operation;
    private String entityType;

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseOperationException(String message, String operation, String entityType) {
        super(message);
        this.operation = operation;
        this.entityType = entityType;
    }

    public DatabaseOperationException(String message, String operation, String entityType, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.entityType = entityType;
    }

    public String getOperation() {
        return operation;
    }

    public String getEntityType() {
        return entityType;
    }
}