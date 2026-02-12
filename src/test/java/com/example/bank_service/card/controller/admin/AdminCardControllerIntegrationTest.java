package com.example.bank_service.card.controller.admin;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.model.UserAccount;
import com.example.bank_service.card.dao.CardRepository;
import com.example.bank_service.card.model.Card;
import com.example.bank_service.card.model.CardStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminCardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        userAccountRepository.deleteAll();

        // Создаём пользователей, т.к. по бизнес-логике карта без пользователя не создаётся
        userAccountRepository.save(new UserAccount("Alice", "pass", "Alice", "Test", new HashSet<>()));
        userAccountRepository.save(new UserAccount("Bob", "pass", "Bob", "Test", new HashSet<>()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMINISTRATOR")
    void createCard_IntegrationSuccess() throws Exception {
        String createCardJson = """
                {
                  "ownerName":"Alice",
                  "expiryDate":"12/29",
                  "balance":100.00
                }
                """;

        mockMvc.perform(post("/card/createNewCard")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCardJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerName").value("Alice"))
                .andExpect(jsonPath("$.cardNumberLast4").exists());

        var cards = cardRepository.findAllByOwnerName("Alice");
        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMINISTRATOR")
    void getAllCards_IntegrationSuccess() throws Exception {
        cardRepository.save(new Card("hash1", "1111", "12/29", "Alice", CardStatus.ACTIVE));
        cardRepository.save(new Card("hash2", "2222", "12/29", "Bob", CardStatus.ACTIVE));

        mockMvc.perform(get("/card/getAllCards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMINISTRATOR")
    void activateCard_IntegrationSuccess() throws Exception {
        Card card = new Card("hash1", "1234", "12/29", "Alice", CardStatus.INACTIVE);
        cardRepository.save(card);

        String searchJson = "{\"ownerName\":\"Alice\", \"cardNumberLast4\":\"1234\"}";

        mockMvc.perform(post("/card/activate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(searchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardStatus").value("ACTIVE"));

        Card updated = cardRepository.findByOwnerNameAndCardNumberLast4("Alice", "1234").get();
        assertThat(updated.getCardStatus()).isEqualTo(CardStatus.ACTIVE);
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMINISTRATOR")
    void deleteCard_IntegrationSuccess() throws Exception {
        Card card = new Card("hash1", "1234", "12/29", "Alice", CardStatus.ACTIVE);
        cardRepository.save(card);

        mockMvc.perform(delete("/card/Alice/deleteCard/1234")
                        .with(csrf()))
                .andExpect(status().isOk());

        assertThat(cardRepository.findByOwnerNameAndCardNumberLast4("Alice", "1234")).isEmpty();
    }
}
