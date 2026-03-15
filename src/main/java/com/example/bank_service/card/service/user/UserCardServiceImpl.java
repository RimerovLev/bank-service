package com.example.bank_service.card.service.user;

import com.example.bank_service.card.dao.CardRepository;
import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.TransferDto;
import com.example.bank_service.card.model.Card;
import com.example.bank_service.card.model.CardStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class UserCardServiceImpl implements UserCardService {
    final CardRepository cardRepository;
    final ModelMapper modelMapper;
    // Simple in-process lock to reduce concurrent transfer races per owner
    private final ConcurrentHashMap<String, Object> transferLocks = new ConcurrentHashMap<>();

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

    @Override
    public boolean transferMoney(String ownerName, TransferDto transferDto) {
        Object lock = transferLocks.computeIfAbsent(ownerName, ignored -> new Object());
        synchronized (lock) {
        // Guard against null payloads
        if (transferDto == null || transferDto.getAmount() == null) {
            throw new IllegalArgumentException("Transfer amount is required");
        }
        // Require positive transfer amount
        if (transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        // Require both card ids
        if (transferDto.getFromCardId() == null || transferDto.getToCardId() == null) {
            throw new IllegalArgumentException("From/To card ids are required");
        }
        // Prevent no-op transfers
        if (transferDto.getFromCardId().equals(transferDto.getToCardId())) {
            throw new IllegalArgumentException("From and To cards must be different");
        }

        Optional<Card> fromCard = cardRepository.findByOwnerNameAndCardNumberLast4(ownerName, transferDto.getFromCardId());
        Optional<Card> toCard = cardRepository.findByOwnerNameAndCardNumberLast4(ownerName, transferDto.getToCardId());
        if(!fromCard.isPresent() || !toCard.isPresent()){
            return false;
        }
        Card from = fromCard.get();
        Card to = toCard.get();
        // Only allow transfers between ACTIVE cards
        if (from.getCardStatus() != CardStatus.ACTIVE || to.getCardStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Both cards must be ACTIVE to transfer");
        }
        BigDecimal fromBalance = from.getBalance() == null ? BigDecimal.ZERO : from.getBalance();
        BigDecimal toBalance = to.getBalance() == null ? BigDecimal.ZERO : to.getBalance();
        if(fromBalance.compareTo(transferDto.getAmount()) < 0){
            throw new IllegalStateException("Not enough balance");
        }
        from.setBalance(fromBalance.subtract(transferDto.getAmount()));
        to.setBalance(toBalance.add(transferDto.getAmount()));
        cardRepository.save(from);
        cardRepository.save(to);

        return true;
        }
    }

    @Override
    public boolean requestCardBlock(String name, String id) {
        Card card = cardRepository.findByOwnerNameAndCardNumberLast4(name, id).orElseThrow(() -> new IllegalArgumentException("Card not found"));
        if(card.getCardStatus() == CardStatus.BLOCK_REQUEST){
            return false;
        }
        card.setCardStatus(CardStatus.BLOCK_REQUEST);
        cardRepository.save(card);
        return true;
    }


}
