package com.example.bank_service.card.service;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.dto.SearchCardDto;
import com.example.bank_service.card.model.Card;

public interface CardService {
    CardDto createCard(CreateCardDto createCardDto);
    Iterable<CardDto> getAllCards();
    Iterable<CardDto> findAllByOwnerName(String ownerName);
    CardDto activateCard(SearchCardDto searchCardDto);
    Card getCardByOwnerAndLast4(SearchCardDto searchCardDto);

    CardDto deleteCard(SearchCardDto searchCardDto);
}
