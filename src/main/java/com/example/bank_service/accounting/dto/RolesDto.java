package com.example.bank_service.accounting.dto;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolesDto {
    String login;
    @Singular
    List<String> roles;
    // End of RolesDto: DTO for roles; carries request/response data for accounting.
}
