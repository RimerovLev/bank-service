package com.example.bank_service.card.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCardDto {
    @NotBlank
    String ownerName;
    @NotBlank
    @Pattern(regexp = "\\d{4}")
    String cardNumberLast4;
}
