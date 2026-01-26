package com.example.bank_service.security.filter;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.model.User;
import com.example.bank_service.accounting.model.UserAccount;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@RequiredArgsConstructor
@Order(60)
public class GetAllCardsFilter implements Filter {
    final UserAccountRepository userAccountRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if(checkEndPoint(httpRequest.getServletPath())) {
            User user = (User) ((HttpServletRequest) request).getUserPrincipal();
            if(!(user.getRoles().contains("ADMINISTRATOR") || user.getRoles().contains("MODERATOR"))) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public boolean checkEndPoint(String path) {
        return  (path.matches("/card/getAllCards")
                || path.matches("/card/findCardsByName/\\w+")
                || path.matches("/card/activate")
                || path.matches("/card/block")
        );
    }
}
