package com.example.bank_service.card.controller.user;

import com.example.bank_service.card.dao.CardRepository;
import com.example.bank_service.card.dto.TransferDto;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserCardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testUser")
    void getUserCards_IntegrationSuccess() throws Exception {
        Card card = new Card("hash1", "1234", "12/29", "testUser", CardStatus.ACTIVE);
        card.setBalance(new BigDecimal("100.00"));
        cardRepository.save(card);

        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cardNumberLast4").value("1234"))
                .andExpect(jsonPath("$.content[0].ownerName").value("testUser"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "testUser")
    void transferMoney_IntegrationSuccess() throws Exception {
        Card fromCard = new Card("hash1", "1111", "12/29", "testUser", CardStatus.ACTIVE);
        fromCard.setBalance(new BigDecimal("100.00"));
        Card toCard = new Card("hash2", "2222", "12/29", "testUser", CardStatus.ACTIVE);
        toCard.setBalance(new BigDecimal("50.00"));
        
        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        String transferJson = "{\"fromCardId\":\"1111\", \"toCardId\":\"2222\", \"amount\":30.0}";

        mockMvc.perform(post("/api/user/cards/transfer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferJson))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        Card updatedFrom = cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1111").get();
        Card updatedTo = cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "2222").get();

        assertThat(updatedFrom.getBalance()).isEqualByComparingTo("70.00");
        assertThat(updatedTo.getBalance()).isEqualByComparingTo("80.00");
    }

    @Test
    @WithMockUser(username = "testUser")
    void requestCardBlock_IntegrationSuccess() throws Exception {
        Card card = new Card("hash1", "1234", "12/29", "testUser", CardStatus.ACTIVE);
        cardRepository.save(card);

        mockMvc.perform(post("/api/user/cards/1234/request-block")
                        .with(csrf()))
                .andExpect(status().isOk());

        Card updatedCard = cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1234").get();
        assertThat(updatedCard.getCardStatus()).isEqualTo(CardStatus.BLOCK_REQUEST);
    }
}
