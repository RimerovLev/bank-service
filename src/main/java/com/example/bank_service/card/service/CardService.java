package com.example.bank_service.card.service;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.dto.SearchCardDto;
import com.example.bank_service.card.dto.TransferDto;
import com.example.bank_service.card.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    CardDto createCard(CreateCardDto createCardDto);
    Iterable<CardDto> getAllCards();
    Iterable<CardDto> findAllByUserLogin(String ownerName);
    CardDto activateCard(SearchCardDto searchCardDto);
    Card getCardByUserLoginAndLast4(String name, String last4);
    CardDto setStatus(String name, String last4, boolean isActive);
    CardDto deleteCard(SearchCardDto searchCardDto);

    Page<CardDto> findCardsByUserLogin(String userLogin, Pageable pageable);

    boolean transfer(String userLogin, TransferDto transferDto);

    boolean requestBlock(String userLogin, String cardNumberLast4);
}
