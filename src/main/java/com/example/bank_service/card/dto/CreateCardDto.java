package com.example.bank_service.card.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateCardDto {
    String userLogin;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry date must be in format MM/yy")
    String expiryDate;

    BigDecimal balance;
}
