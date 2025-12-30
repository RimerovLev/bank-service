package com.example.bank_service.accounting.service;

import com.example.bank_service.accounting.dto.EditUserDto;
import com.example.bank_service.accounting.dto.RolesDto;
import com.example.bank_service.accounting.dto.UserDto;
import com.example.bank_service.accounting.dto.UserRegisterDto;

public interface UserAccountService {
    UserDto register(UserRegisterDto userRegisterDto);
    UserDto deleteUser(String login);
    UserDto updateUser(String login ,EditUserDto editUserDto);
    RolesDto changeRolesList(String login ,String role, boolean isAddRole);
    void changePassword(String login, String newPassword);
    UserDto getUser(String login);
}
