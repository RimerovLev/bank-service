package com.example.bank_service.card.dao;

import com.example.bank_service.card.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends MongoRepository<Card, String> {
    List<Card> findAllByUserLogin(String ownerName);
    Optional<Card> findByUserLoginAndCardNumberLast4(String ownerName, String cardNumberLast4);
    Page<Card> findAllByUserLogin(String userLogin, Pageable pageable);

    void deleteAllByUserLogin(String login);
}
