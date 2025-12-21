package com.example.bank_service.dao;

import com.example.bank_service.model.Card;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends MongoRepository<Card, String> {
    List<Card> findAllByOwnerName(String ownerName);
    Optional<Card> findByOwnerNameAndCardNumberLast4(String ownerName, String cardNumberLast4);
}
