package com.example.bank_service.security.filter;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.model.Roles;
import com.example.bank_service.accounting.model.User;
import com.example.bank_service.accounting.model.UserAccount;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(50)

public class CreateCardFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if(checkEndPoint(req.getMethod(), req.getServletPath())) {
//            UserAccount userAccount = userAccountRepository.findById(req.getUserPrincipal().getName()).get();
            User user = (User) ((HttpServletRequest) request).getUserPrincipal();
            if(!user.getRoles().contains(Roles.ADMINISTRATOR)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public boolean checkEndPoint(String method, String path) {
        return HttpMethod.POST.matches(method) && path.matches("/card/createNewCard");
    }
}
