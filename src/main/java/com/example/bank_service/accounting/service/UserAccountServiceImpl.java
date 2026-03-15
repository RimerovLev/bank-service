package com.example.bank_service.accounting.service;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.dto.EditUserDto;
import com.example.bank_service.accounting.dto.RolesDto;
import com.example.bank_service.accounting.dto.UserDto;
import com.example.bank_service.accounting.dto.UserRegisterDto;
import com.example.bank_service.accounting.dto.exceptions.UserExistException;
import com.example.bank_service.accounting.dto.exceptions.UserNotFoundException;
import com.example.bank_service.accounting.model.Roles;
import com.example.bank_service.accounting.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService, CommandLineRunner {
    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    // Bootstrap admin must be explicit to avoid default credentials in production
    @Value("${bank.bootstrap-admin.enabled:false}")
    private boolean bootstrapAdminEnabled;
    // Allow overriding default login via config for safer deployments
    @Value("${bank.bootstrap-admin.login:admin}")
    private String bootstrapAdminLogin;
    // Require a non-empty password when bootstrap is enabled
    @Value("${bank.bootstrap-admin.password:}")
    private String bootstrapAdminPassword;


    @Override
    public UserDto register(UserRegisterDto userRegisterDto) {
        if(userAccountRepository.existsById(userRegisterDto.getLogin())){
            System.out.println(userRegisterDto.getLogin());
            throw new UserExistException("User with login: " + userRegisterDto.getLogin() + " already exists");
        }
            UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
            String password = passwordEncoder.encode(userRegisterDto.getPassword());
            userAccount.setPassword(password);
            userAccountRepository.save(userAccount);
            return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto deleteUser(String login) {
        if(!userAccountRepository.existsById(login)){
            throw new UserNotFoundException("User with login: " + login + " not found");
        }
        UserDto userDto = modelMapper.map(userAccountRepository.findById(login).get(), UserDto.class);
        userAccountRepository.deleteById(login);
        return userDto;
    }

    @Override
    public UserDto updateUser(String login, EditUserDto editUserDto) {
        if(!userAccountRepository.existsById(login)){
            throw new UserNotFoundException("User with login: " + login + " not found");
        }
        UserAccount userAccount = userAccountRepository.findById(login).get();
        userAccount.setFirstName(editUserDto.getFirstName());
        userAccount.setLastName(editUserDto.getLastName());
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
        if(!userAccountRepository.existsById(login)){
            throw new UserNotFoundException("User with login: " + login + " not found");
        }
        UserAccount userAccount = userAccountRepository.findById(login).get();
        if(isAddRole){
            userAccount.addRole(validateRole(role));
            userAccountRepository.save(userAccount);
            return modelMapper.map(userAccount, RolesDto.class);
        }
        userAccount.removeRole(validateRole(role));
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, RolesDto.class);
    }

    @Override
    public void changePassword(String login, String newPassword) {
        if(!userAccountRepository.existsById(login)){
            throw new UserNotFoundException("User with login: " + login + " not found");
        }
        UserAccount userAccount = userAccountRepository.findById(login).get();
        userAccount.setPassword(passwordEncoder.encode(newPassword));
        userAccountRepository.save(userAccount);
    }

    @Override
    public UserDto getUser(String login) {
        if(!userAccountRepository.existsById(login)){
            throw new UserNotFoundException("User with login: " + login + " not found");
        }
        return modelMapper.map(userAccountRepository.findById(login).get(), UserDto.class);
    }

    private String validateRole(String role) {
        try {
            return Roles.valueOf(role.toUpperCase()).name();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown role: " + role);
        }
    }



    @Override
    public void run(String... args) throws Exception{
        // Skip creating admin unless explicitly enabled
        if (!bootstrapAdminEnabled) {
            return;
        }
        // Fail fast if password is missing to prevent weak defaults
        if (bootstrapAdminPassword == null || bootstrapAdminPassword.isBlank()) {
            throw new IllegalStateException("bank.bootstrap-admin.password must be set when bootstrap admin is enabled");
        }
        if(!userAccountRepository.existsById(bootstrapAdminLogin)){
           String password = passwordEncoder.encode(bootstrapAdminPassword);
           UserAccount userAccount = new UserAccount(bootstrapAdminLogin, password, "", "",
                   Set.of(Roles.ADMINISTRATOR, Roles.MODERATOR, Roles.USER));
           userAccountRepository.save(userAccount);
        }
    }
    // End of UserAccountServiceImpl: Service implementation for user account; centralizes business rules for accounting.
}
