package com.example.bank_service.card.dto;
import lombok.Getter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Getter
public class CreateCardDto {
    @NotBlank
    String ownerName;
    // Enforce MM/YY format to match service validation
    @NotBlank
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}")
    String expiryDate;
    @NotNull
    @PositiveOrZero
    BigDecimal balance;

}
