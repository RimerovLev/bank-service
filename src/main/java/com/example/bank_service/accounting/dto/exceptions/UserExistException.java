package com.example.bank_service.accounting.dto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exist")
public class UserExistException extends RuntimeException {
    public static final long serialVersionUID = 1L;

    public UserExistException() {
        super("User already exist");
    }

}
