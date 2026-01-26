package com.example.bank_service.security.filter;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;

@Component
@RequiredArgsConstructor
@Order(30)
public class UpdateByOwnerFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if(checkEndPoint(request.getMethod(), request.getServletPath())) {
            Principal principal = request.getUserPrincipal();
            String[] arr = request.getServletPath().split("/");
            String username = arr[arr.length - 1];
            if (!principal.getName().equalsIgnoreCase(username)) {
                response.sendError(403, "Forbidden");
                return;
            }
        }
        chain.doFilter(request, response);
    }
    public boolean checkEndPoint(String method, String path) {
        return HttpMethod.PUT.matches(method) && path.matches("/account/user/\\w+");
    }
}
