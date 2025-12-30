package com.example.bank_service.card.dto;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class CreateCardDto {
    String ownerName;
    String expiryDate;
    BigDecimal balance;

}
