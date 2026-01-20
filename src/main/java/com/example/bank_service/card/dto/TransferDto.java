package com.example.bank_service.card.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransferDto {
    private String fromCardId;
    private String toCardId;
    private BigDecimal amount;
}
