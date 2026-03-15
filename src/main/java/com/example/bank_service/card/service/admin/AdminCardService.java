package com.example.bank_service.card.service.admin;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.dto.SearchCardDto;
import com.example.bank_service.card.model.Card;
import org.springframework.data.domain.Page;

public interface AdminCardService {
    CardDto createCard(CreateCardDto createCardDto);
    // Paginate to avoid unbounded reads on large datasets
    Page<CardDto> getAllCards(int page, int size);
    // Paginate to protect admins from large result sets
    Page<CardDto> findAllByOwnerName(String ownerName, int page, int size);
    CardDto activateCard(SearchCardDto searchCardDto);
    Card getCardByOwnerAndLast4(SearchCardDto searchCardDto);
    CardDto deleteCard(String ownerName, String cardNumberLast4);
}
