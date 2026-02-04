package com.example.bank_service.accounting.controller;

import com.example.bank_service.accounting.dto.EditUserDto;
import com.example.bank_service.accounting.dto.RolesDto;
import com.example.bank_service.accounting.dto.UserDto;
import com.example.bank_service.accounting.dto.UserRegisterDto;
import com.example.bank_service.accounting.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor

public class UserAccountController {

    final UserAccountService userAccountService;


    @PostMapping("/register")
    public UserDto register(@RequestBody UserRegisterDto userRegisterDto) {
        return userAccountService.register(userRegisterDto);
    }

    @PostMapping("/login")
    public UserDto login(Principal principal) {
        return userAccountService.getUser(principal.getName());
    }

    @DeleteMapping("/user/{login}")
    public UserDto delete(@PathVariable String login) {
        return userAccountService.deleteUser(login);
    }

    @PutMapping("/user/{login}")
    public UserDto update(@PathVariable String login, @RequestBody EditUserDto editUserDto) {
        return userAccountService.updateUser(login, editUserDto);
    }

    @PutMapping("/user/{login}/role/{role}")
    public RolesDto updateRole(@PathVariable String login, @PathVariable String role) {
        return userAccountService.changeRolesList(login, role, true);
    }

    @DeleteMapping("/user/{login}/role/{role}")
    public RolesDto deleteRole(@PathVariable String login, @PathVariable String role) {
        return userAccountService.changeRolesList(login, role.toUpperCase(), false);
    }

    @GetMapping("/user/{login}")
    public UserDto getUser(@PathVariable String login) {
        return userAccountService.getUser(login);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(Principal principal, @RequestHeader("X-Password") String newPassword) {
         userAccountService.changePassword(principal.getName(), newPassword);

    }



}
