package com.example.bank_service.accounting.dto;

import com.example.bank_service.accounting.model.Roles;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolesDto {
    String login;
    @Singular
    List<Roles> roles;
}
