package com.example.bank_service.security.filter;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.model.Roles;
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
@Order(20)
public class AdminManagingFilter implements Filter {
    final UserAccountRepository userAccountRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp; //кастинг сервлетов
        if(checkEndPoint(request.getMethod(), request.getServletPath())) {
            UserAccount userAccount = userAccountRepository.findById(request.getUserPrincipal().getName()).get();
            if(!userAccount.getRoles().contains(Roles.ADMINISTRATOR)) {
                response.sendError(403, "Permission denied");
                return;
            }
        }
        chain.doFilter(request, response);
    }
    private boolean checkEndPoint(String method, String path) {
        return path.matches("/account/user/\\w+/role/\\w+");
    }
}
