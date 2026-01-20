package com.example.bank_service.card.controller;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.service.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.bank_service.accounting.dao.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import com.example.bank_service.accounting.model.UserAccount;
import java.util.Optional;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

@WebMvcTest(AdminCardController.class)
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserAccountRepository userAccountRepository;

    private final String authHeader = "Basic YWRtaW46cGFzc3dvcmQ=";

    @BeforeEach
    void setUp() {
        UserAccount mockUser = mock(UserAccount.class);

        // "admin" matches the decoded username from the header
        when(mockUser.getLogin()).thenReturn("admin");

        // This is the EXACT BCrypt hash for the string "password"
        // AuthFilter needs this to pass BCrypt.checkpw("password", hash)
        when(mockUser.getPassword()).thenReturn("$2a$10$vCh8W99YDwXvX.7p7Vqv/.2QZSlSgNVD6.m7SpsvVbgLxUIdfN6S.");

        // AdminManagingFilter needs this to pass the role check
        when(mockUser.getRoles()).thenReturn(new java.util.HashSet<>(java.util.Collections.singletonList("ADMINISTRATOR")));

        // Return this mock for ANY findById call (used by both filters and the service)
        Mockito.when(userAccountRepository.findById(anyString())).thenReturn(Optional.of(mockUser));
    }

    /**
     * Tests the createCard functionality of the AdminCardController.
     * Verifies that valid input creates a card and returns a correct CardDto response.
     */
    @Test
    void createCard_WithValidInput_ShouldReturnCreatedCard() throws Exception {
        // Arrange
        CreateCardDto createCardDto = new CreateCardDto();
        createCardDto.setUserLogin("user123");
        createCardDto.setExpiryDate("12/28");
        createCardDto.setBalance(new BigDecimal("5000"));

        CardDto cardDto = new CardDto("1234", "12/28", "user123",
                com.example.bank_service.card.model.CardStatus.INACTIVE,
                new BigDecimal("10000"));

        Mockito.when(cardService.createCard(any(CreateCardDto.class))).thenReturn(cardDto);

        String requestBody = """
                    {
                        "userLogin": "user123",
                        "expiryDate": "12/28",
                        "balance": 5000
                    }
                """;
        String expectedResponse = """
                    {
                        "cardNumberLast4": "1234",
                        "expiryDate": "12/28",
                        "userLogin": "user123",
                        "cardStatus": "INACTIVE",
                        "balance": 10000
                    }
                """;

        // Act and Assert
        mockMvc.perform(post("/card/createNewCard")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    /**
     * Tests the createCard functionality with invalid expiry date.
     * Verifies that the API returns a bad request error.
     */
    @Test
    void createCard_WithInvalidExpiryDate_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String requestBody = """
                    {
                        "userLogin": "user123",
                        "expiryDate": "invalid-date",
                        "balance": 5000
                    }
                """;

        // Act and Assert
        mockMvc.perform(post("/card/createNewCard")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the createCard functionality with missing required fields.
     * Verifies that the API returns a bad request error when mandatory fields are omitted.
     */
    @Test
    void createCard_WithMissingFields_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String requestBody = """
                    {
                        "expiryDate": "12/28",
                        "balance": 5000
                    }
                """;

        // Act and Assert
        mockMvc.perform(post("/card/createNewCard")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}