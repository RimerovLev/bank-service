package com.example.bank_service.accounting.dto;

import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Getter
public class EditUserDto {
    // Require non-empty names to avoid blank updates
    @NotBlank
    @Size(max = 50)
    String firstName;
    // Require non-empty names to avoid blank updates
    @NotBlank
    @Size(max = 50)
    String lastName;
}
