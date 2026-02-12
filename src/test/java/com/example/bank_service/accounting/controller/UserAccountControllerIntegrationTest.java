package com.example.bank_service.accounting.controller;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.model.Roles;
import com.example.bank_service.accounting.model.UserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userAccountRepository.deleteAll();
    }

    @Test
    void register_IntegrationSuccess() throws Exception {
        String registerJson = """
                {
                  "login":"newUser",
                  "password":"password123",
                  "firstName":"New",
                  "lastName":"User"
                }
                """;

        mockMvc.perform(post("/account/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("newUser"))
                .andExpect(jsonPath("$.firstName").value("New"));

        UserAccount saved = userAccountRepository.findById("newUser").orElseThrow();
        assertThat(passwordEncoder.matches("password123", saved.getPassword())).isTrue();
    }

    @Test
    @WithMockUser(username = "testUser")
    void login_IntegrationSuccess() throws Exception {
        userAccountRepository.save(new UserAccount("testUser", "hashedPass", "Test", "User", Set.of(Roles.USER)));

        mockMvc.perform(post("/account/login")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testUser"));
    }

    // ... existing code ...
    @Test
    @WithMockUser(username = "testUser")
    void getUser_IntegrationSuccess() throws Exception {
        userAccountRepository.save(new UserAccount("testUser", "hashedPass", "Test", "User", Set.of(Roles.USER)));

        mockMvc.perform(get("/account/user/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("testUser"));
    }
// ... existing code ...

    @Test
    @WithMockUser(roles = "ADMINISTRATOR")
    void update_IntegrationSuccess() throws Exception {
        userAccountRepository.save(new UserAccount("testUser", "hashedPass", "Old", "Name", Set.of(Roles.USER)));

        String updateJson = "{\"firstName\":\"New\", \"lastName\":\"Name\"}";

        mockMvc.perform(put("/account/user/testUser")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"));

        UserAccount updated = userAccountRepository.findById("testUser").get();
        assertThat(updated.getFirstName()).isEqualTo("New");
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMINISTRATOR")
    void updateRole_IntegrationSuccess() throws Exception {
        userAccountRepository.save(new UserAccount("testUser", "hashedPass", "Test", "User", Set.of(Roles.USER)));

        mockMvc.perform(put("/account/user/testUser/role/ADMINISTRATOR")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").value(org.hamcrest.Matchers.hasItem("ADMINISTRATOR")));

        UserAccount updated = userAccountRepository.findById("testUser").get();
        assertThat(updated.getRoles()).contains(Roles.ADMINISTRATOR);
    }

    @Test
    @WithMockUser(username = "testUser")
    void updatePassword_IntegrationSuccess() throws Exception {
        userAccountRepository.save(new UserAccount("testUser", passwordEncoder.encode("oldPass"), "Test", "User", Set.of(Roles.USER)));

        mockMvc.perform(put("/account/password")
                        .with(csrf())
                        .header("X-Password", "newPassword"))
                .andExpect(status().isNoContent());

        UserAccount updated = userAccountRepository.findById("testUser").get();
        assertThat(passwordEncoder.matches("newPassword", updated.getPassword())).isTrue();
    }
}
