package com.example.bank_service.accounting.dto;

import lombok.Getter;

@Getter
public class UserRegisterDto {
    String login;
    String password;
    String firstName;
    String lastName;
}
