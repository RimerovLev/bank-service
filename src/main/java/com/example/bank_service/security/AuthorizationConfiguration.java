package com.example.bank_service.security;

import com.example.bank_service.accounting.model.Roles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
public class AuthorizationConfiguration {

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.POST, "/account/register").permitAll()
                        .requestMatchers(
                                "/account/user/*/role/*",
                                "card/createNewCard",
                                "card/getAllCards",
                                "/card/findCardsByName/{name}",
                                "/card/activate",
                                "/card/block",
                                "/card/deleteCard"
                        ).hasAuthority(Roles.ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.GET, "/account/user/{user}")
                            .access(new WebExpressionAuthorizationManager("authentication.principal.username == #user"))
                        .anyRequest().authenticated()
                )
                // ВАЖНО: настройка сессий
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }
}
