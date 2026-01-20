package com.example.bank_service.card.service;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.dto.exceptions.UserNotFoundException;
import com.example.bank_service.accounting.model.UserAccount;
import com.example.bank_service.card.dao.CardRepository;
import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.model.Card;
import com.example.bank_service.card.model.CardStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void createCard_UserExists_CreatesCardAndReturnsCardDto() {
        // Arrange
        CreateCardDto createCardDto = new CreateCardDto();
        createCardDto.setUserLogin("testUser");
        createCardDto.setExpiryDate("12/30");
        createCardDto.setBalance(new BigDecimal("5000"));

        UserAccount userAccount = new UserAccount();
        userAccount.setLogin("testUser");

        Card card = new Card();
        card.setUserLogin("testUser");
        card.setCardNumberLast4("1234");
        card.setExpiryDate("12/30");
        card.setCardStatus(CardStatus.INACTIVE);
        card.setBalance(new BigDecimal("10000"));

        CardDto cardDto = new CardDto();
        cardDto.setCardNumberLast4("1234");
        cardDto.setExpiryDate("12/30");
        cardDto.setUserLogin("testUser");
        cardDto.setCardStatus(CardStatus.INACTIVE);
        cardDto.setBalance(new BigDecimal("10000"));

        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(modelMapper.map(any(Card.class), eq(CardDto.class))).thenReturn(cardDto);
        when(modelMapper.map(any(CreateCardDto.class), eq(Card.class))).thenReturn(card);

        // Act
        CardDto result = cardService.createCard(createCardDto);

        // Assert
        assertNotNull(result);
        assertEquals("1234", result.getCardNumberLast4());
        assertEquals("12/30", result.getExpiryDate());
        assertEquals("testUser", result.getUserLogin());
        assertEquals(CardStatus.INACTIVE, result.getCardStatus());
        assertEquals(new BigDecimal("10000"), result.getBalance());

        verify(userAccountRepository, times(1)).findById("testUser");
        verify(cardRepository, times(1)).save(any(Card.class));
        verify(modelMapper, times(1)).map(any(Card.class), eq(CardDto.class));
    }

    @Test
    void createCard_UserDoesNotExist_ThrowsUserNotFoundException() {
        // Arrange
        CreateCardDto createCardDto = new CreateCardDto();
        createCardDto.setUserLogin("nonExistingUser");
        createCardDto.setExpiryDate("12/30");
        createCardDto.setBalance(new BigDecimal("5000"));

        when(userAccountRepository.findById("nonExistingUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> cardService.createCard(createCardDto));

        verify(userAccountRepository, times(1)).findById("nonExistingUser");
        verifyNoInteractions(cardRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void createCard_InvalidExpiryDate_ThrowsIllegalArgumentException() {
        // Arrange
        CreateCardDto createCardDto = new CreateCardDto();
        createCardDto.setUserLogin("testUser");
        createCardDto.setExpiryDate("01/20"); // Expired date
        createCardDto.setBalance(new BigDecimal("5000"));

        UserAccount userAccount = new UserAccount();
        userAccount.setLogin("testUser");

        when(userAccountRepository.findById("testUser")).thenReturn(Optional.of(userAccount));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> cardService.createCard(createCardDto));

        verify(userAccountRepository, times(1)).findById("testUser");
        verifyNoInteractions(cardRepository);
        verifyNoInteractions(modelMapper);
    }
}