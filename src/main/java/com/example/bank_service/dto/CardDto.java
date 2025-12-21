package com.example.bank_service.dto;
import com.example.bank_service.model.CardStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.YearMonth;

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
