package com.example.bank_service.accounting.service;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.dto.UserDto;
import com.example.bank_service.accounting.dto.UserRegisterDto;
import com.example.bank_service.accounting.dto.exceptions.UserExistException;
import com.example.bank_service.accounting.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserAccountServiceImplTest {

    @Autowired
    private UserAccountServiceImpl userAccountService;

    @MockBean
    private UserAccountRepository userAccountRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    void testRegister_Success() {
        // Arrange
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setLogin("testuser");
        userRegisterDto.setPassword("password123");
        userRegisterDto.setFirstName("John");
        userRegisterDto.setLastName("Doe");

        UserAccount userAccount = new UserAccount();
        userAccount.setLogin(userRegisterDto.getLogin());
        userAccount.setPassword(userRegisterDto.getPassword());
        userAccount.setFirstName(userRegisterDto.getFirstName());
        userAccount.setLastName(userRegisterDto.getLastName());

        UserDto userDto = new UserDto();
        userDto.setLogin(userAccount.getLogin());
        userDto.setFirstName(userAccount.getFirstName());
        userDto.setLastName(userAccount.getLastName());

        when(userAccountRepository.existsById(userRegisterDto.getLogin())).thenReturn(false);
        when(modelMapper.map(userRegisterDto, UserAccount.class)).thenReturn(userAccount);
        when(modelMapper.map(userAccount, UserDto.class)).thenReturn(userDto);

        // Act
        UserDto result = userAccountService.register(userRegisterDto);

        // Assert
        assertNotNull(result);
        assertEquals(userRegisterDto.getLogin(), result.getLogin());
        assertEquals(userRegisterDto.getFirstName(), result.getFirstName());
        assertEquals(userRegisterDto.getLastName(), result.getLastName());
        verify(userAccountRepository, times(1)).existsById(userRegisterDto.getLogin());
        verify(userAccountRepository, times(1)).save(userAccount);
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Arrange
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setLogin("testuser");
        userRegisterDto.setPassword("password123");
        userRegisterDto.setFirstName("John");
        userRegisterDto.setLastName("Doe");

        when(userAccountRepository.existsById(userRegisterDto.getLogin())).thenReturn(true);

        // Act & Assert
        assertThrows(UserExistException.class, () -> userAccountService.register(userRegisterDto));
        verify(userAccountRepository, times(1)).existsById(userRegisterDto.getLogin());
        verify(userAccountRepository, never()).save(any(UserAccount.class));
    }

    @Test
    void testRegister_HashPasswordAndSave() {
        // Arrange
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setLogin("secureUser");
        userRegisterDto.setPassword("securePassword");
        userRegisterDto.setFirstName("Alice");
        userRegisterDto.setLastName("Smith");

        UserAccount userAccount = new UserAccount();
        userAccount.setLogin(userRegisterDto.getLogin());
        userAccount.setFirstName(userRegisterDto.getFirstName());
        userAccount.setLastName(userRegisterDto.getLastName());

        UserDto userDto = new UserDto();
        userDto.setLogin(userRegisterDto.getLogin());
        userDto.setFirstName(userRegisterDto.getFirstName());
        userDto.setLastName(userRegisterDto.getLastName());

        when(userAccountRepository.existsById(userRegisterDto.getLogin())).thenReturn(false);
        when(modelMapper.map(userRegisterDto, UserAccount.class)).thenReturn(userAccount);
        when(modelMapper.map(userAccount, UserDto.class)).thenReturn(userDto);

        // Act
        UserDto result = userAccountService.register(userRegisterDto);

        // Assert
        assertNotNull(result);
        assertEquals(userRegisterDto.getLogin(), result.getLogin());
        assertNotEquals(userRegisterDto.getPassword(), userAccount.getPassword());
        verify(userAccountRepository, times(1)).save(userAccount);
    }
}