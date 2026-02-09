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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @BeforeEach
    void setUp() {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Test
    void register_Success() {
        UserRegisterDto registerDto = createRegisterDto("testUser", "password");
        when(userAccountRepository.existsById("testUser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        UserDto result = userAccountService.register(registerDto);

        assertNotNull(result);
        assertEquals("testUser", result.getLogin());
        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        UserRegisterDto registerDto = createRegisterDto("testUser", "password");
        when(userAccountRepository.existsById("testUser")).thenReturn(true);

        assertThrows(UserNotFoundException.class, () -> userAccountService.register(registerDto));
    }

    @Test
    void deleteUser_Success() {
        UserAccount userAccount = new UserAccount("testUser", "pass", "First", "Last", new HashSet<>(Set.of(Roles.USER)));
        when(userAccountRepository.existsById("testUser")).thenReturn(true);
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));

        UserDto result = userAccountService.deleteUser("testUser");

        assertEquals("testUser", result.getLogin());
        verify(userAccountRepository).deleteById("testUser");
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userAccountRepository.existsById("testUser")).thenReturn(false);

        assertThrows(UserExistException.class, () -> userAccountService.deleteUser("testUser"));
    }

    @Test
    void updateUser_Success() {
        UserAccount userAccount = new UserAccount("testUser", "pass", "First", "Last", new HashSet<>(Set.of(Roles.USER)));
        EditUserDto editDto = createEditUserDto("NewFirst", "NewLast");
        when(userAccountRepository.existsById("testUser")).thenReturn(true);
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));

        UserDto result = userAccountService.updateUser("testUser", editDto);

        assertEquals("NewFirst", result.getFirstName());
        assertEquals("NewLast", result.getLastName());
        verify(userAccountRepository).save(userAccount);
    }

    @Test
    void changeRolesList_AddRole() {
        UserAccount userAccount = new UserAccount("testUser", "pass", "First", "Last", new HashSet<>(Set.of(Roles.USER)));
        when(userAccountRepository.existsById("testUser")).thenReturn(true);
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));

        RolesDto result = userAccountService.changeRolesList("testUser", "ADMINISTRATOR", true);

        assertTrue(result.getRoles().contains("ADMINISTRATOR"));
        verify(userAccountRepository).save(userAccount);
    }

    @Test
    void changePassword_Success() {
        UserAccount userAccount = new UserAccount("testUser", "pass", "First", "Last", new HashSet<>(Set.of(Roles.USER)));
        when(userAccountRepository.existsById("testUser")).thenReturn(true);
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        userAccountService.changePassword("testUser", "newPass");

        assertEquals("encodedNewPass", userAccount.getPassword());
        verify(userAccountRepository).save(userAccount);
    }

    @Test
    void getUser_Success() {
        UserAccount userAccount = new UserAccount("testUser", "pass", "First", "Last", new HashSet<>(Set.of(Roles.USER)));
        when(userAccountRepository.existsById("testUser")).thenReturn(true);
        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));

        UserDto result = userAccountService.getUser("testUser");

        assertEquals("testUser", result.getLogin());
    }

    private UserRegisterDto createRegisterDto(String login, String password) {
        UserRegisterDto dto = new UserRegisterDto();
        try {
            var loginField = UserRegisterDto.class.getDeclaredField("login");
            loginField.setAccessible(true);
            loginField.set(dto, login);
            var passField = UserRegisterDto.class.getDeclaredField("password");
            passField.setAccessible(true);
            passField.set(dto, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dto;
    }

    private EditUserDto createEditUserDto(String first, String last) {
        EditUserDto dto = new EditUserDto();
        try {
            var firstField = EditUserDto.class.getDeclaredField("firstName");
            firstField.setAccessible(true);
            firstField.set(dto, first);
            var lastField = EditUserDto.class.getDeclaredField("lastName");
            lastField.setAccessible(true);
            lastField.set(dto, last);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dto;
    }
}
