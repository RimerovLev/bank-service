package com.example.bank_service.card.service.admin;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.card.dao.CardRepository;
import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.dto.SearchCardDto;
import com.example.bank_service.card.model.Card;
import com.example.bank_service.card.model.CardStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCardServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private CardRepository cardRepository;

    private ModelMapper modelMapper;

    private AdminCardServiceImpl adminCardService;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        adminCardService = new AdminCardServiceImpl(userAccountRepository, cardRepository, modelMapper);
    }

    @Test
    void createCard_Success() {
        String futureDate = YearMonth.now().plusYears(1).format(DateTimeFormatter.ofPattern("MM/yy"));
        CreateCardDto dto = createCreateCardDto("testUser", futureDate, BigDecimal.valueOf(100));

        when(userAccountRepository.existsById("testUser")).thenReturn(true);

        CardDto result = adminCardService.createCard(dto);

        assertNotNull(result);
        assertEquals("testUser", result.getOwnerName());
        assertEquals(CardStatus.INACTIVE, result.getCardStatus());
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_UserNotFound_ThrowsException() {
        CreateCardDto dto = createCreateCardDto("nonExistent", "12/26", BigDecimal.ZERO);
        when(userAccountRepository.existsById("nonExistent")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> adminCardService.createCard(dto));
    }

    @Test
    void createCard_PastDate_ThrowsException() {
        String pastDate = YearMonth.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM/yy"));
        CreateCardDto dto = createCreateCardDto("testUser", pastDate, BigDecimal.ZERO);
        when(userAccountRepository.existsById("testUser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> adminCardService.createCard(dto));
    }

    @Test
    void activateCard_Success() {
        SearchCardDto searchDto = new SearchCardDto("testUser", "1234");
        Card card = new Card("hash", "1234", "12/26", "testUser", CardStatus.INACTIVE);

        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1234")).thenReturn(Optional.of(card));

        CardDto result = adminCardService.activateCard(searchDto);

        assertEquals(CardStatus.ACTIVE, result.getCardStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard_Success() {
        SearchCardDto searchDto = new SearchCardDto("testUser", "1234");
        Card card = new Card("hash", "1234", "12/26", "testUser", CardStatus.ACTIVE);

        when(cardRepository.findByOwnerNameAndCardNumberLast4("testUser", "1234")).thenReturn(Optional.of(card));

        CardDto result = adminCardService.deleteCard(searchDto);

        assertNotNull(result);
        verify(cardRepository).delete(card);
    }

    private CreateCardDto createCreateCardDto(String owner, String expiry, BigDecimal balance) {
        CreateCardDto dto = new CreateCardDto();
        try {
            var ownerField = CreateCardDto.class.getDeclaredField("ownerName");
            ownerField.setAccessible(true);
            ownerField.set(dto, owner);
            var expiryField = CreateCardDto.class.getDeclaredField("expiryDate");
            expiryField.setAccessible(true);
            expiryField.set(dto, expiry);
            var balanceField = CreateCardDto.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(dto, balance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dto;
    }
}