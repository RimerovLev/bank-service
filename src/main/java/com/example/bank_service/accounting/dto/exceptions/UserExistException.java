package com.example.bank_service.accounting.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserExistException extends RuntimeException {
    public static final long serialVersionUID = 1L;
    public UserExistException(String message) {
        super(message);
    }
    // End of UserExistException: Exception for user exist; signals domain errors for accounting.
}
