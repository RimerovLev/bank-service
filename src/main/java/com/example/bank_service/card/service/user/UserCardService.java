package com.example.bank_service.card.service.user;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.SearchCardDto;
import com.example.bank_service.card.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

public interface UserCardService {
    Page<CardDto> getUserCards(String name, int page, int size);

    CardDto getCardById(String ownerName, String last4);

    BigDecimal getCardBalance(String ownerName, String id);
}
