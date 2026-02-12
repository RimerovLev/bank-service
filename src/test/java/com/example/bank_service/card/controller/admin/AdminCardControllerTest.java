package com.example.bank_service.card.controller.admin;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.model.CardStatus;
import com.example.bank_service.card.service.admin.AdminCardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCardController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminCardService cardService;

    @Autowired
    private org.modelmapper.ModelMapper modelMapper;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @org.springframework.context.annotation.Bean
        public org.modelmapper.ModelMapper modelMapper() {
            return new org.modelmapper.ModelMapper();
        }
    }

    @Test
    void createCard_Success() throws Exception {
        CardDto response = new CardDto("1234", "12/29", "Alice", CardStatus.ACTIVE, new BigDecimal("100.00"));
        when(cardService.createCard(any())).thenReturn(response);

        mockMvc.perform(post("/card/createNewCard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ownerName":"Alice",
                                  "expiryDate":"12/29",
                                  "balance":100.00
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumberLast4").value("1234"))
                .andExpect(jsonPath("$.ownerName").value("Alice"))
                .andExpect(jsonPath("$.cardStatus").value("ACTIVE"));
    }

    @Test
    void getAllCards_Success() throws Exception {
        CardDto dto = new CardDto("9876", "01/30", "Bob", CardStatus.BLOCKED, new BigDecimal("0.00"));
        when(cardService.getAllCards()).thenReturn(List.of(dto));

        mockMvc.perform(get("/card/getAllCards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardNumberLast4").value("9876"))
                .andExpect(jsonPath("$[0].ownerName").value("Bob"))
                .andExpect(jsonPath("$[0].cardStatus").value("BLOCKED"));
    }

    @Test
    void findCardsByName_Success() throws Exception {
        CardDto dto = new CardDto("5555", "11/28", "Alice", CardStatus.INACTIVE, new BigDecimal("10.00"));
        when(cardService.findAllByOwnerName(eq("Alice"))).thenReturn(List.of(dto));

        mockMvc.perform(get("/card/findCardsByName/Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerName").value("Alice"))
                .andExpect(jsonPath("$[0].cardNumberLast4").value("5555"));
    }

    @Test
    void activateCard_Success() throws Exception {
        CardDto activated = new CardDto("1111", "10/27", "Alice", CardStatus.ACTIVE, new BigDecimal("15.00"));
        when(cardService.activateCard(any())).thenReturn(activated);

        mockMvc.perform(post("/card/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ownerName":"Alice",
                                  "cardNumberLast4":"1111"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.cardNumberLast4").value("1111"));
    }

    @Test
    void deleteCard_Success() throws Exception {
        CardDto deleted = new CardDto("2222", "09/27", "Alice", CardStatus.INACTIVE, new BigDecimal("0.00"));
        when(cardService.deleteCard(eq("Alice"), eq("2222"))).thenReturn(deleted);

        mockMvc.perform(delete("/card/Alice/deleteCard/2222"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumberLast4").value("2222"))
                .andExpect(jsonPath("$.ownerName").value("Alice"));
    }
}
