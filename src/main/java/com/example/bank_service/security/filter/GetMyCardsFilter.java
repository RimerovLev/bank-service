package com.example.bank_service.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
@Component
@RequiredArgsConstructor
@Order(70)
public class GetMyCardsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getServletPath().startsWith("/api/user/cards")
        || req.getServletPath().startsWith("/api/user/card/{id}")
        || req.getServletPath().startsWith("/api/user/cards/{id}/request-block")) {
            Principal principal = req.getUserPrincipal();

            if (principal == null || principal.getName().isBlank()) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            req.setAttribute("userLogin", principal.getName());
        }

        chain.doFilter(req, res);
    }
}
