package com.letrasvivas.bookapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BusinessValidationException extends RuntimeException {

    private String field;
    private Object rejectedValue;
    private String validationCode;

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, String field, Object rejectedValue) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public BusinessValidationException(String message, String field, Object rejectedValue, String validationCode) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.validationCode = validationCode;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public String getValidationCode() {
        return validationCode;
    }
}