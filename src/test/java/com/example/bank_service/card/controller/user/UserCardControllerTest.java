package com.example.bank_service.card.controller.user;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.TransferDto;
import com.example.bank_service.card.model.CardStatus;
import com.example.bank_service.card.service.user.UserCardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCardController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCardService userCardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser")
    void getUserCards_Success() throws Exception {
        CardDto cardDto = new CardDto("1234", "12/29", "testUser", CardStatus.ACTIVE, new BigDecimal("100.00"));
        Page<CardDto> cardPage = new PageImpl<>(List.of(cardDto), PageRequest.of(0, 10), 1);

        when(userCardService.getUserCards(eq("testUser"), anyInt(), anyInt())).thenReturn(cardPage);

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
    void getUserCardById_Success() throws Exception {
        CardDto cardDto = new CardDto("1234", "12/29", "testUser", CardStatus.ACTIVE, new BigDecimal("100.00"));

        when(userCardService.getCardById("testUser", "1234")).thenReturn(cardDto);

        mockMvc.perform(get("/api/user/cards/1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumberLast4").value("1234"))
                .andExpect(jsonPath("$.ownerName").value("testUser"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void getUserCardBalance_Success() throws Exception {
        BigDecimal balance = new BigDecimal("100.00");

        when(userCardService.getCardBalance("testUser", "1234")).thenReturn(balance);

        mockMvc.perform(get("/api/user/cards/1234/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(100.0)); // проверяем числовое значение
    }


    @Test
    @WithMockUser(username = "testUser")
    void transferMoney_Success() throws Exception {
        when(userCardService.transferMoney(eq("testUser"), any(TransferDto.class))).thenReturn(true);

        mockMvc.perform(post("/api/user/cards/transfer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromCardId\":\"1234\", \"toCardId\":\"5678\", \"amount\":50.0}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void requestCardBlock_Success() throws Exception {
        when(userCardService.requestCardBlock("testUser", "1234")).thenReturn(true);

        mockMvc.perform(post("/api/user/cards/1234/request-block")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
