package com.example.bank_service.card.dao;

import com.example.bank_service.card.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends MongoRepository<Card, String> {
    List<Card> findAllByOwnerName(String ownerName);
    Optional<Card> findByOwnerNameAndCardNumberLast4(String ownerName, String cardNumberLast4);
    Page<Card> findByOwnerName(String ownerName, Pageable pageable);
    // Used to prevent collisions when generating card numbers
    boolean existsByCardNumberHash(String cardNumberHash);
    // End of CardRepository: Repository for card; encapsulates MongoDB access for card.
}
