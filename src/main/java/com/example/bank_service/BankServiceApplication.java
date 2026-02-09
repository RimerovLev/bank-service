package com.example.bank_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class BankServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(BankServiceApplication.class, args);

	}

}
