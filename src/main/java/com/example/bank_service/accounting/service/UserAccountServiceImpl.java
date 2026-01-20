package com.example.bank_service.accounting.service;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.dto.EditUserDto;
import com.example.bank_service.accounting.dto.RolesDto;
import com.example.bank_service.accounting.dto.UserDto;
import com.example.bank_service.accounting.dto.UserRegisterDto;
import com.example.bank_service.accounting.dto.exceptions.UserExistException;
import com.example.bank_service.accounting.model.UserAccount;
import com.example.bank_service.card.dao.CardRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService, CommandLineRunner {
    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;
    private final CardRepository cardRepository;


    /**
     * Registers user; persists hashed password; returns user data
     */
    @Override
    public UserDto register(UserRegisterDto userRegisterDto) {
        if(userAccountRepository.existsById(userRegisterDto.getLogin())){
            throw new UserExistException();
        }
        UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
        String password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
        userAccount.setPassword(password);
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    /**
     * Deletes user and associated cards; returns deleted user
     */
    @Override
    public UserDto deleteUser(String login) {
        if(!userAccountRepository.existsById(login)){
            throw new UserExistException();
        }
        cardRepository.deleteAllByUserLogin(login);
        UserDto userDto = modelMapper.map(userAccountRepository.findById(login).get(), UserDto.class);
        userAccountRepository.deleteById(login);
        return userDto;
    }

    @Override
    public UserDto updateUser(String login, EditUserDto editUserDto) {
        if(!userAccountRepository.existsById(login)){
            throw new UserExistException();
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
            throw new UserExistException();
        }
        UserAccount userAccount = userAccountRepository.findById(login).get();
        if(isAddRole){
            userAccount.addRole(role.toUpperCase());
            userAccountRepository.save(userAccount);
            return modelMapper.map(userAccount, RolesDto.class);
        }
        userAccount.removeRole(role.toUpperCase());
        userAccountRepository.save(userAccount);
        return modelMapper.map(userAccount, RolesDto.class);
    }

    @Override
    public void changePassword(String login, String newPassword) {
        if(!userAccountRepository.existsById(login)){
            throw new UserExistException();
        }
        UserAccount userAccount = userAccountRepository.findById(login).get();
        String password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        userAccount.setPassword(password);
        userAccountRepository.save(userAccount);
    }

    @Override
    public  UserDto getUser(String login) {
        if(!userAccountRepository.existsById(login)){
            throw new UserExistException();
        }
        return modelMapper.map(userAccountRepository.findById(login).get(), UserDto.class);
    }

    @Override
    public void run(String... args) throws Exception {

        if(!userAccountRepository.existsById("ADMINISTRATOR")){
            String password = BCrypt.hashpw("admin", BCrypt.gensalt());
            UserAccount userAccount = new UserAccount("admin", password, "", "");
            userAccount.addRole("ADMINISTRATOR");
            userAccount.addRole("MODERATOR");
            userAccountRepository.save(userAccount);
        }
    }
}
