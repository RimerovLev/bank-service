package com.example.bank_service.security;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomSecurityConfiguration {
    final UserAccountRepository userAccountRepository;

    public boolean checkUser(String username, String login){
        UserAccount userAccount = userAccountRepository.findById(username).orElse(null);
        return userAccount != null && userAccount.getLogin().equals(login);
    }
}
