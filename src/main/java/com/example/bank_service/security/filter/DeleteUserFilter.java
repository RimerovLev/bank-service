package com.example.bank_service.security.filter;

import com.example.bank_service.security.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
@RequiredArgsConstructor
@Component
@Order(40)

public class DeleteUserFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (checkEndPoint(req.getMethod(), req.getServletPath())) {

            Principal principal = req.getUserPrincipal();
            String name = req.getServletPath().split("/")[1];
            User user = (User) ((HttpServletRequest) request).getUserPrincipal();
            if(!(user.getName().equals(name) || user.getRoles().contains("ADMINISTRATOR"))) {
                res.sendError(403);
                return;
            }
        }
        chain.doFilter(request, response);
    }
    public boolean checkEndPoint(String method, String url) {
        return HttpMethod.DELETE.matches(method) && url.matches("/account/user/\\w+");
    }
}
