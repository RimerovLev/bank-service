package com.example.bank_service.card.service.user;

import com.example.bank_service.card.dao.CardRepository;
import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.TransferDto;
import com.example.bank_service.card.model.Card;
import com.example.bank_service.card.model.CardStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    private ModelMapper modelMapper;

    private UserCardServiceImpl userCardService;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        userCardService = new UserCardServiceImpl(cardRepository, modelMapper);
    }

    @Test
    void getUserCards_Success() {
        Card card = createCard("1234", "testUser", BigDecimal.valueOf(100));
        Page<Card> page = new PageImpl<>(List.of(card));
        when(cardRepository.findByOwnerName(eq("testUser"), any(PageRequest.class))).thenReturn(page);

        Page<CardDto> result = userCardService.getUserCards("testUser", 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("1234", result.getContent().get(0).getCardNumberLast4());
    }

    @Test
    void getCardById_Success() {
        Card card = createCard("1234", "testUser", BigDecimal.valueOf(100));
        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1234")).thenReturn(Optional.of(card));

        CardDto result = userCardService.getCardById("testUser", "1234");

        assertNotNull(result);
        assertEquals("1234", result.getCardNumberLast4());
    }

    @Test
    void getCardById_NotFound_ThrowsException() {
        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1234")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userCardService.getCardById("testUser", "1234"));
    }

    @Test
    void getCardBalance_Success() {
        Card card = createCard("1234", "testUser", BigDecimal.valueOf(150.50));
        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1234")).thenReturn(Optional.of(card));

        BigDecimal balance = userCardService.getCardBalance("testUser", "1234");

        assertEquals(0, BigDecimal.valueOf(150.50).compareTo(balance));
    }

    @Test
    void transferMoney_Success() {
        Card fromCard = createCard("1111", "testUser", BigDecimal.valueOf(100));
        Card toCard = createCard("2222", "testUser", BigDecimal.valueOf(50));
        TransferDto transferDto = createTransferDto("1111", "2222", BigDecimal.valueOf(30));

        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1111")).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "2222")).thenReturn(Optional.of(toCard));

        boolean result = userCardService.transferMoney("testUser", transferDto);

        assertTrue(result);
        assertEquals(0, BigDecimal.valueOf(70).compareTo(fromCard.getBalance()));
        assertEquals(0, BigDecimal.valueOf(80).compareTo(toCard.getBalance()));
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transferMoney_InsufficientBalance_ThrowsException() {
        Card fromCard = createCard("1111", "testUser", BigDecimal.valueOf(20));
        Card toCard = createCard("2222", "testUser", BigDecimal.valueOf(50));
        TransferDto transferDto = createTransferDto("1111", "2222", BigDecimal.valueOf(30));

        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1111")).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "2222")).thenReturn(Optional.of(toCard));

        assertThrows(IllegalStateException.class, () -> userCardService.transferMoney("testUser", transferDto));
    }

    @Test
    void requestCardBlock_Success() {
        Card card = createCard("1234", "testUser", BigDecimal.ZERO);
        card.setCardStatus(CardStatus.ACTIVE);
        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1234")).thenReturn(Optional.of(card));

        boolean result = userCardService.requestCardBlock("testUser", "1234");

        assertTrue(result);
        assertEquals(CardStatus.BLOCK_REQUEST, card.getCardStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void requestCardBlock_AlreadyRequested_ReturnsFalse() {
        Card card = createCard("1234", "testUser", BigDecimal.ZERO);
        card.setCardStatus(CardStatus.BLOCK_REQUEST);
        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1234")).thenReturn(Optional.of(card));

        boolean result = userCardService.requestCardBlock("testUser", "1234");

        assertFalse(result);
        verify(cardRepository, never()).save(any());
    }

    private Card createCard(String last4, String owner, BigDecimal balance) {
        Card card = new Card("hash", last4, "12/26", owner, CardStatus.ACTIVE);
        card.setBalance(balance);
        return card;
    }

    private TransferDto createTransferDto(String from, String to, BigDecimal amount) {
        TransferDto dto = new TransferDto();
        try {
            var fromField = TransferDto.class.getDeclaredField("fromCardId");
            fromField.setAccessible(true);
            fromField.set(dto, from);
            var toField = TransferDto.class.getDeclaredField("toCardId");
            toField.setAccessible(true);
            toField.set(dto, to);
            var amountField = TransferDto.class.getDeclaredField("amount");
            amountField.setAccessible(true);
            amountField.set(dto, amount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dto;
    }
}