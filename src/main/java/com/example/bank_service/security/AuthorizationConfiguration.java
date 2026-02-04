package com.example.bank_service.security;

import com.example.bank_service.accounting.model.Roles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthorizationConfiguration {

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.authorizeRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/account/register")
                    .permitAll()
                .requestMatchers("/account/user/*/role/*",
                                            "card/createNewCard",
                                            "card/getAllCards",
                                            "/card/findCardsByName/{name}",
                                            "/card/activate",
                                            "/card/block",
                                            "/card/deleteCard")
                    .hasAuthority(Roles.ADMINISTRATOR.name()).anyRequest()
                    .authenticated()
        );
        return http.build();
    }
}
