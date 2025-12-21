package com.example.bank_service;

import com.example.bank_service.dao.CardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BankServiceApplicationTests {

	@Autowired
	private CardRepository cardRepository;

	@Test
	void contextLoads() {
		// просто проверка контекста
	}
}

