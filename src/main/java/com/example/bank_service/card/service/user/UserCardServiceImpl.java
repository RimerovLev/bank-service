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
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

    @Override
    public boolean transferMoney(String ownerName, TransferDto transferDto) {
        Optional<Card> fromCard = cardRepository.findByOwnerNameAndCardNumberLast4(ownerName, transferDto.getFromCardId());
        Optional<Card> toCard = cardRepository.findByOwnerNameAndCardNumberLast4(ownerName, transferDto.getToCardId());
        if(!fromCard.isPresent() || !toCard.isPresent()){
            return false;
        }
        Card from = fromCard.get();
        Card to = toCard.get();
        if(from.getBalance().compareTo(transferDto.getAmount()) < 0){
            throw new IllegalStateException("Not enough balance");
        }
        from.setBalance(from.getBalance().subtract(transferDto.getAmount()));
        to.setBalance(to.getBalance().add(transferDto.getAmount()));
        cardRepository.save(from);
        cardRepository.save(to);

        return true;
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
