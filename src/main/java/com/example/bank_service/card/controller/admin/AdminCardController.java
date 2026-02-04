package com.example.bank_service.card.controller.admin;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.dto.CreateCardDto;
import com.example.bank_service.card.dto.SearchCardDto;
import com.example.bank_service.card.service.admin.AdminCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class AdminCardController {

    final AdminCardService cardService;

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
