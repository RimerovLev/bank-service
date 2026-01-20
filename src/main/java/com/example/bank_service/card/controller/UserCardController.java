package com.example.bank_service.card.controller;


import com.example.bank_service.card.dto.TransferDto;
import com.example.bank_service.card.model.Card;
import com.example.bank_service.card.service.CardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;
    private final ModelMapper getModelMapper;

@GetMapping({"/cards", "/cards/{id}"})
public Object getUserCardsOrCard(HttpServletRequest request,
                                 @PathVariable(name = "id", required = false) String cardNumberLast4,
                                 Pageable pageable) {
    String userLogin = (String) request.getAttribute("userLogin");
    if (userLogin == null) {
        throw new RuntimeException("User not found in request");
    }

    if (cardNumberLast4 != null) {
        return cardService.getCardByUserLoginAndLast4(userLogin, cardNumberLast4);
    } else {
        return cardService.findCardsByUserLogin(userLogin, pageable);
    }
}
@GetMapping("/cards/{id}/balance")
    public BigDecimal getBalance(HttpServletRequest request, @PathVariable(name = "id", required = false) String cardNumberLast4) {
    String userLogin = (String) request.getAttribute("userLogin");
    if (userLogin == null) {
        throw new RuntimeException("User not found in request");
    }
    if (cardNumberLast4.isBlank()) {
        throw new RuntimeException("Card not found in request");
    }
    return cardService.getCardByUserLoginAndLast4(userLogin, cardNumberLast4).getBalance();

}
@PostMapping("/cards/transfer")
    public boolean transfer(@RequestBody TransferDto transferDto, HttpServletRequest request) {
    String userLogin = (String) request.getAttribute("userLogin");
    if (userLogin == null) {
        throw new RuntimeException("User not found in request");
    }
    return cardService.transfer(userLogin, transferDto);
}
@PostMapping("/cards/{id}/request-block")
    public boolean requestBlock(HttpServletRequest request, @PathVariable(name = "id", required = false) String cardNumberLast4) {
    String userLogin = (String) request.getAttribute("userLogin");
    if (userLogin == null) {
        throw new RuntimeException("User not found in request");
    }
    if (cardNumberLast4.isBlank()) {
        throw new RuntimeException("Card not found in request");
    }
    return cardService.requestBlock(userLogin, cardNumberLast4);
}

}