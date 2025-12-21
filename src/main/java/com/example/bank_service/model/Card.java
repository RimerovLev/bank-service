package com.example.bank_service.model;

import com.example.bank_service.utils.HashUtil;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.YearMonth;


@Getter
@EqualsAndHashCode(of = "id")
@Document(collection = "cards")
@NoArgsConstructor
public class Card {
    private String id;
    @Setter
    private String cardNumberHash;
    @Setter
    private String cardNumberLast4;
    @Setter
    private String expiryDate;
    private String ownerName;
    @Setter
    @Getter
    private CardStatus cardStatus;
    private BigDecimal balance;



    public Card(String cardNumberHash, String cardNumberLast4, String expiryDate, String ownerName, CardStatus cardStatus) {
        this.cardNumberHash = cardNumberHash;
        this.cardNumberLast4 = cardNumberLast4;
        this.expiryDate = expiryDate;
        this.ownerName = ownerName;
        this.cardStatus = cardStatus;
        this.balance = BigDecimal.ZERO;
    }

    private String hashCardNumber(String cardNumber) {
        String hash = HashUtil.hashPassword(cardNumber);
        System.out.println(hash);
       return hash;
    }


    public void changeStatus(CardStatus newStatus) {
        this.cardStatus = newStatus;
    }

    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void subBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
}
