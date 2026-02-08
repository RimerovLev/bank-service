package com.example.bank_service.card.service.user;

import com.example.bank_service.card.dao.CardRepository;
import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.model.Card;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserCardServiceImpl implements UserCardService {
    final CardRepository cardRepository;
    final ModelMapper modelMapper;

    @Override
    public Page<CardDto> getUserCards(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findByOwnerName(name, pageable)
                .map(card -> modelMapper.map(card, CardDto.class));
    }

    @Override
    public CardDto getCardById(String ownerName, String last4) {
       Optional<CardDto> cardDto = cardRepository.findByOwnerNameAndCardNumberLast4(ownerName, last4)
               .map(card -> modelMapper.map(card, CardDto.class));
        return cardDto.orElseThrow(() -> new IllegalArgumentException("Card not found"));
    }

    @Override
    public BigDecimal getCardBalance(String ownerName, String id) {
        return getCardById(ownerName, id).getBalance();
    }


}
