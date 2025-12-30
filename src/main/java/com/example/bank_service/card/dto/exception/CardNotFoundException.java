package com.example.bank_service.card.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CardNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public CardNotFoundException(String message) {
        super("Card not found");
    }
}
