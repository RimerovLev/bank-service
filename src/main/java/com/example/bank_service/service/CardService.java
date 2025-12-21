package com.example.bank_service.service;

import com.example.bank_service.dto.CardDto;
import com.example.bank_service.dto.CreateCardDto;
import com.example.bank_service.dto.SearchCardDto;
import com.example.bank_service.model.Card;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    CardDto createCard(CreateCardDto createCardDto);
    Iterable<CardDto> getAllCards();
    Iterable<CardDto> findAllByOwnerName(String ownerName);
    CardDto activateCard(SearchCardDto searchCardDto);
    public Card getCardByOwnerAndLast4(SearchCardDto searchCardDto);

    CardDto deleteCard(SearchCardDto searchCardDto);
}
