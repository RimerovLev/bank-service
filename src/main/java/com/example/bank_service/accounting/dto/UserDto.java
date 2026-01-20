package com.example.bank_service.accounting.dto;


import com.example.bank_service.accounting.model.Roles;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class UserDto {
    String login;
    String firstName;
    String lastName;
    @Singular
    List<Roles> roles;
}
