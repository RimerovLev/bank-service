package com.example.bank_service.accounting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Principal;
import java.util.Set;

@AllArgsConstructor
@Getter
public class User implements Principal {
    private String name;
    private Set<Roles> roles;


}
