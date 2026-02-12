package com.example.bank_service.card.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCardDto {
    String ownerName;
    String cardNumberLast4;
}
