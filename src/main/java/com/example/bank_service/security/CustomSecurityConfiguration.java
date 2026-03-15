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
    // End of CustomSecurityConfiguration: Helper used by security rules to verify resource ownership.
    // It loads the user by login and checks that the authenticated principal is allowed to access the target login,
    // which prevents users from reading or modifying other users' data.
}
