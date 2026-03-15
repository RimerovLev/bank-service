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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class AdminCardServiceImpl implements AdminCardService {
    final UserAccountRepository userAccountRepository;
    final CardRepository cardRepository;
    final ModelMapper modelMapper;
    private static final int CARD_NUMBER_LENGTH = 16;
    private static final int MAX_CARD_NUMBER_ATTEMPTS = 10;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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
    public Page<CardDto> getAllCards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findAll(pageable)
                .map(card -> modelMapper.map(card, CardDto.class));
    }


    @Override
    public Page<CardDto> findAllByOwnerName(String ownerName, int page, int size) {
        if(!userAccountRepository.existsById(ownerName)){
            throw new IllegalArgumentException("User not found");
        }
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findByOwnerName(ownerName, pageable)
                .map(card -> modelMapper.map(card, CardDto.class));
    }

    @Override
    public CardDto activateCard(SearchCardDto searchCardDto) {
        Card card = getCardByOwnerAndLast4(searchCardDto);
        card.setCardStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        return modelMapper.map(card, CardDto.class);
    }

    @Override
    public CardDto deleteCard(String ownerName, String cardNumberLast4) {
        Card card = getCardByOwnerAndLast4(modelMapper.map(new SearchCardDto(ownerName, cardNumberLast4), SearchCardDto.class));
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
        for (int attempt = 0; attempt < MAX_CARD_NUMBER_ATTEMPTS; attempt++) {
            StringBuilder sb = new StringBuilder(CARD_NUMBER_LENGTH);
            for (int i = 0; i < CARD_NUMBER_LENGTH; i++) {
                sb.append(SECURE_RANDOM.nextInt(10));
            }
            String number = sb.toString();
            String hash = HashUtil.hashPassword(number);
            if (!cardRepository.existsByCardNumberHash(hash)) {
                return number;
            }
        }
        throw new IllegalStateException("Failed to generate unique card number");
    }
}
