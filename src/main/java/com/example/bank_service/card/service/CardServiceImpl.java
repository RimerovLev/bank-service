package com.example.bank_service.card.service;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.dto.exceptions.UserNotFoundException;
import com.example.bank_service.accounting.model.UserAccount;
import com.example.bank_service.card.dao.CardRepository;
import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.dto.SearchCardDto;
import com.example.bank_service.card.dto.TransferDto;
import com.example.bank_service.card.model.Card;
import com.example.bank_service.card.model.CardStatus;
import com.example.bank_service.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    final CardRepository cardRepository;
    final UserAccountRepository userAccountRepository;
    final ModelMapper modelMapper;

    @Override
    public CardDto createCard(CreateCardDto createCardDto) {

        UserAccount user = userAccountRepository.findById(createCardDto.getUserLogin())
                .orElseThrow(UserNotFoundException::new);
            String expiryDate = checkDate(createCardDto.getExpiryDate());
            String rawNumber = generateCardNumber();
            String cardHash = HashUtil.hashPassword(rawNumber);
            String last4 = rawNumber.substring(rawNumber.length() - 4);
            Card card = modelMapper.map(createCardDto, Card.class);
            card.setUserLogin(user.getLogin());
            card.setExpiryDate(expiryDate);
            card.setCardNumberHash(cardHash);
            card.setCardNumberLast4(last4);
            card.setCardStatus(CardStatus.INACTIVE);
            card.setBalance(new BigDecimal("10000"));
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
    public Iterable<CardDto> findAllByUserLogin(String userLogin) {
        return cardRepository.findAllByUserLogin(userLogin)
                .stream()
                .map(card -> modelMapper.map(card, CardDto.class))
                .toList();
    }

    @Override
    public Page<CardDto> findCardsByUserLogin(String userLogin, Pageable pageable) {
        if(!userAccountRepository.findById(userLogin).isPresent()) {
            throw new UserNotFoundException();
        }
        Page<Card> cardsPage = cardRepository.findAllByUserLogin(userLogin, pageable);
        return cardsPage.map(card -> modelMapper.map(card, CardDto.class));
    }

    @Override
    public boolean transfer(String userLogin, TransferDto transferDto) {
        List<Card> cards = cardRepository.findAllByUserLogin(userLogin);
        Card fromCard = cards.stream()
                .filter(c -> c.getCardNumberLast4().equals(transferDto.getFromCardId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
        Card toCard = cards.stream()
                .filter(c -> c.getCardNumberLast4().equals(transferDto.getToCardId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
        if(fromCard == null || toCard == null) {
            return false;
        }
        if(fromCard.getBalance().compareTo(transferDto.getAmount())<0){
            return false;
        }
        fromCard.subBalance(transferDto.getAmount());
        toCard.addBalance(transferDto.getAmount());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        return true;
    }

    @Override
    public boolean requestBlock(String userLogin, String cardNumberLast4) {
        Card card = getCardByUserLoginAndLast4(userLogin, cardNumberLast4);
        card.setCardStatus(CardStatus.BLOCK_REQUEST);
        cardRepository.save(card);
        return true;
    }

    @Override
    public CardDto activateCard(SearchCardDto searchCardDto) {
        Card card = getCardByUserLoginAndLast4(searchCardDto.getUserLogin(), searchCardDto.getCardNumberLast4());
        card.setCardStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
        return modelMapper.map(card, CardDto.class);
    }

    @Override
    public CardDto deleteCard(SearchCardDto searchCardDto) {
        Card card = getCardByUserLoginAndLast4(searchCardDto.getUserLogin(), searchCardDto.getCardNumberLast4());
        CardDto cardDto = modelMapper.map(card, CardDto.class);
        cardRepository.delete(card);
        return cardDto;
    }



    public CardDto setStatus(String name, String last4, boolean isAddStatus){
        Card card = getCardByUserLoginAndLast4(name, last4);
        if(isAddStatus){
            card.setCardStatus(CardStatus.ACTIVE);
            cardRepository.save(card);
            CardDto cardDto = modelMapper.map(card, CardDto.class);
            return cardDto;
        }else {
            card.setCardStatus(CardStatus.BLOCKED);
            cardRepository.save(card);
            CardDto cardDto = modelMapper.map(card, CardDto.class);
            return cardDto;
        }
    }



    @Override
    public Card getCardByUserLoginAndLast4(String name, String last4) {
        Card card = cardRepository.findByUserLoginAndCardNumberLast4(name,
                last4).orElseThrow(()-> new IllegalArgumentException("Card not found"));
        return card;
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
