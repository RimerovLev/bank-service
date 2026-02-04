package com.example.bank_service.security;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findById(username).orElseThrow(()
                -> new UsernameNotFoundException("User not found with username: " + username));
        String[] rolesArray = userAccount.getRoles()
                .stream()
                .map(Enum::name)
                .toArray(String[]::new);
        return new User(username,userAccount.getPassword(),
                AuthorityUtils.createAuthorityList(rolesArray));
    }
}
