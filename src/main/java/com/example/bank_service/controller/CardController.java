package com.example.bank_service.controller;

import com.example.bank_service.dto.CardDto;
import com.example.bank_service.dto.CreateCardDto;
import com.example.bank_service.dto.SearchCardDto;
import com.example.bank_service.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/card")

public class CardController {

    final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

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
        return cardService.activateCard(searchCardDto);
    }

    @DeleteMapping("/deleteCard")
    public CardDto deleteCard(@RequestBody SearchCardDto searchCardDto) {
        return cardService.deleteCard(searchCardDto);
    }
}
