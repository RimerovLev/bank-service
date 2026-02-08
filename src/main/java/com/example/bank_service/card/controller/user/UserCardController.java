package com.example.bank_service.card.controller.user;

import com.example.bank_service.card.dto.CardDto;
import com.example.bank_service.card.service.user.UserCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserCardController {
    final UserCardService userCardService;

    @GetMapping("/cards")
    public Page<CardDto> getUserCards(Authentication authentication,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size
    ) {
        return userCardService.getUserCards(authentication.getName(), page, size);
    }

    @GetMapping("/cards/{id}")
    public CardDto getUserCardById(Authentication authentication,@PathVariable String id) {
        return userCardService.getCardById(authentication.getName(), id);
    }

    @GetMapping("/cards/{id}/balance")
    public BigDecimal getUserCardBalance(Authentication authentication,@PathVariable String id) {
        return userCardService.getCardBalance(authentication.getName(), id);
    }
}
