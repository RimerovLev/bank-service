package com.example.bank_service.card.controller;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.dto.SearchCardDto;
import com.example.bank_service.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

    final CardService cardService;

    @PostMapping("/createNewCard")
    public CardDto createCard(@RequestBody CreateCardDto dto) {
        return cardService.createCard(dto);
    }

    @GetMapping("/getAllCards")
    public Iterable<CardDto> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/findCardsByName/{name}")
    public Iterable<CardDto> findCardsByName(@PathVariable String name) {
        return cardService.findAllByOwnerName(name);
    }

    @PostMapping("/activate")
    public CardDto activateCard(@RequestBody SearchCardDto searchCardDto) {
        return cardService.setStatus(searchCardDto.getOwnerName(),  searchCardDto.getCardNumberLast4(), true);
    }
    @PostMapping("/block")
    public CardDto blockCard(@RequestBody SearchCardDto searchCardDto) {
        return cardService.setStatus(searchCardDto.getOwnerName(),  searchCardDto.getCardNumberLast4(), false);
    }

    @DeleteMapping("/deleteCard")
    public CardDto deleteCard(@RequestBody SearchCardDto searchCardDto) {
        return cardService.deleteCard(searchCardDto);
    }
}
