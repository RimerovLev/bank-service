package com.example.bank_service.security.model;

import com.example.bank_service.accounting.model.Roles;
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
