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
                        // OpenAPI/Swagger endpoints should be publicly accessible for docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Explicitly protect admin-only user updates/deletes with exact paths
                        .requestMatchers(HttpMethod.PUT, "/account/user/{login}").hasAuthority(Roles.ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.DELETE, "/account/user/{login}").hasAuthority(Roles.ADMINISTRATOR.name())
                        .requestMatchers(
                                // Ant matchers use * instead of {var}; keep patterns aligned with real routes
                                "/account/user/*/role/*",
                                "/card/createNewCard",
                                "/card/getAllCards",
                                "/card/findCardsByName/*",
                                "/card/activate",
                                "/card/block",
                                "/card/*/deleteCard/*"
                        ).hasAuthority(Roles.ADMINISTRATOR.name())
                        .requestMatchers(HttpMethod.GET, "/account/user/{login}")
                            .access(new WebExpressionAuthorizationManager("authentication.principal.username == #login"))
                        .anyRequest().authenticated()
                )
                // ВАЖНО: настройка сессий
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }
    // End of AuthorizationConfiguration: Defines the SecurityFilterChain, enabling HTTP Basic auth, disabling CSRF for stateless API use,
    // and applying fine-grained route authorization (public registration, admin-only management, and self-only profile reads).
    // Also explicitly whitelists Swagger/OpenAPI endpoints so documentation is reachable without credentials.
}
