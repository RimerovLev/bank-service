package com.example.bank_service.card.dto;

import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
@Getter
public class TransferDto {
    @NotBlank
    @Pattern(regexp = "\\d{4}")
    String fromCardId;
    @NotBlank
    @Pattern(regexp = "\\d{4}")
    String toCardId;
    @NotNull
    @Positive
    BigDecimal amount;
}
