package com.example.bank_service.card.dto;

import lombok.Getter;

import java.math.BigDecimal;
@Getter
public class TransferDto {
    String fromCardId;
    String toCardId;
    BigDecimal amount;
}
