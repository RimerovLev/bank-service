package com.example.bank_service.card.service.admin;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.card.dao.CardRepository;
import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.dto.SearchCardDto;
import com.example.bank_service.card.model.Card;
import com.example.bank_service.card.model.CardStatus;
import com.example.bank_service.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class AdminCardServiceImpl implements AdminCardService {
    final UserAccountRepository userAccountRepository;
    final CardRepository cardRepository;
    final ModelMapper modelMapper;

    @Override
    public CardDto createCard(CreateCardDto createCardDto) {
        if(!userAccountRepository.existsById(createCardDto.getOwnerName())){
            throw new IllegalArgumentException("User not found");
        }
        String rawNumber = generateCardNumber();
        String cardHash = HashUtil.hashPassword(rawNumber);
        Card card = modelMapper.map(createCardDto, Card.class);
        card.setExpiryDate(checkDate(createCardDto.getExpiryDate()));
        card.setCardNumberHash(cardHash);
        card.setCardNumberLast4(rawNumber.substring(rawNumber.length() - 4));
        card.setCardStatus(CardStatus.INACTIVE);
        cardRepository.save(card);
        return modelMapper.map(card, CardDto.class);
    }

    @Override
    public Iterable<CardDto> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(card -> modelMapper.map(card, CardDto.class))
                .toList();
    }


    @Override
    public Iterable<CardDto> findAllByOwnerName(String ownerName) {
        if(!userAccountRepository.existsById(ownerName)){
            throw new IllegalArgumentException("User not found");
        }
        return cardRepository.findAllByOwnerName(ownerName)
                .stream()
                .map(card -> modelMapper.map(card, CardDto.class))
                .toList();
    }

    @Override
    public CardDto activateCard(SearchCardDto searchCardDto) {
        Card card = getCardByOwnerAndLast4(searchCardDto);
        card.setCardStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        return modelMapper.map(card, CardDto.class);
    }

    @Override
    public CardDto deleteCard(SearchCardDto searchCardDto) {
        Card card = getCardByOwnerAndLast4(searchCardDto);
        CardDto cardDto = modelMapper.map(card, CardDto.class);
        cardRepository.delete(card);
        return cardDto;
    }




    @Override
    public Card getCardByOwnerAndLast4(SearchCardDto searchCardDto) {
        return cardRepository.findByOwnerNameAndCardNumberLast4(searchCardDto.getOwnerName(), searchCardDto.getCardNumberLast4())
                .orElseThrow(()-> new IllegalArgumentException("Card not found"));

    }


    private String checkDate(String expiryDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth yearMonth;

        try {
            yearMonth = YearMonth.parse(expiryDate, formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Expiry date must be in format MM/YY");
        }


        if (yearMonth.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Card expiration date is in the past");
        }
        return expiryDate;
    }

    private  String generateCardNumber() {
        return String.valueOf((long)(Math.random() * 1_0000_0000_0000_0000L));
    }
}
