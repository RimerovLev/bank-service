package com.example.bank_service.accounting.dto;

import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Getter
public class UserRegisterDto {
    // Prevent empty/short logins
    @NotBlank
    @Size(min = 3, max = 50)
    String login;
    // Enforce minimum password length and avoid blank values
    @NotBlank
    @Size(min = 8, max = 72)
    String password;
    // Limit length to keep user profile data bounded
    @Size(max = 50)
    String firstName;
    // Limit length to keep user profile data bounded
    @Size(max = 50)
    String lastName;
}
