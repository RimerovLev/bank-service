package com.example.bank_service.accounting.controller;

import com.example.bank_service.accounting.dto.EditUserDto;
import com.example.bank_service.accounting.dto.RolesDto;
import com.example.bank_service.accounting.dto.UserDto;
import com.example.bank_service.accounting.dto.UserRegisterDto;
import com.example.bank_service.accounting.service.UserAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountService userAccountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void register_Success() throws Exception {
        UserRegisterDto registerDto = new UserRegisterDto(); // Login, password etc are private and no setters, but Jackson uses fields
        UserDto userDto = UserDto.builder().login("test").firstName("First").lastName("Last").build();

        when(userAccountService.register(any(UserRegisterDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/account/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"test\", \"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("test"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void login_Success() throws Exception {
        UserDto userDto = UserDto.builder().login("testUser").build();
        when(userAccountService.getUser("testUser")).thenReturn(userDto);

        mockMvc.perform(post("/account/login").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testUser"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void delete_Success() throws Exception {
        UserDto userDto = UserDto.builder().login("targetUser").build();
        when(userAccountService.deleteUser("targetUser")).thenReturn(userDto);

        mockMvc.perform(delete("/account/user/targetUser").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("targetUser"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void update_Success() throws Exception {
        UserDto userDto = UserDto.builder().login("testUser").firstName("NewName").build();
        when(userAccountService.updateUser(eq("testUser"), any(EditUserDto.class))).thenReturn(userDto);

        mockMvc.perform(put("/account/user/testUser")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"NewName\", \"lastName\":\"NewLast\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("NewName"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMINISTRATOR")
    void updateRole_Success() throws Exception {
        RolesDto rolesDto = RolesDto.builder().login("testUser").role("ADMINISTRATOR").build();
        when(userAccountService.changeRolesList(eq("testUser"), eq("ADMINISTRATOR"), eq(true))).thenReturn(rolesDto);

        mockMvc.perform(put("/account/user/testUser/role/ADMINISTRATOR").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("ADMINISTRATOR"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void getUser_Success() throws Exception {
        UserDto userDto = UserDto.builder().login("testUser").build();
        when(userAccountService.getUser("testUser")).thenReturn(userDto);

        mockMvc.perform(get("/account/user/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testUser"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void updatePassword_Success() throws Exception {
        mockMvc.perform(put("/account/password")
                        .with(csrf())
                        .header("X-Password", "newPassword"))
                .andExpect(status().isNoContent());
    }
}
