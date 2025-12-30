package com.example.bank_service.card.dto;
import com.example.bank_service.card.model.CardStatus;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {
     String cardNumberLast4;
     String  expiryDate;
     String ownerName;
     CardStatus cardStatus;
     BigDecimal balance;
}
