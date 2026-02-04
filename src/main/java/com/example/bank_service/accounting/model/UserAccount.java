package com.example.bank_service.accounting.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Getter
@Document(collection = "users")

public class UserAccount {
    @Id
    String login;
    @Setter
    String password;
    @Setter
    String firstName;
    @Setter
    String lastName;
    @Setter
    Set<Roles> roles;


    public UserAccount(){
        roles = new HashSet<>();
        roles.add(Roles.USER);
    }

    public UserAccount(String login, String password, String firstName, String lastName, Set<Roles> roles) {
        this();
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    public boolean addRole(String role) {
        return roles.add(Roles.valueOf(role));
    }
    public boolean removeRole(String role) {
        return roles.remove(Roles.valueOf(role));
    }
}
