package com.example.bank_service.security.filter;

import com.example.bank_service.accounting.dao.UserAccountRepository;
import com.example.bank_service.accounting.dto.exceptions.UserNotFoundException;
import com.example.bank_service.accounting.model.Roles;
import com.example.bank_service.security.context.SecuriryContext;
import com.example.bank_service.security.model.User;
import com.example.bank_service.accounting.model.UserAccount;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(10)
public class AuthFilter implements Filter {

    private final UserAccountRepository userAccountRepository;
    private final SecuriryContext securityContext;


    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse res = (HttpServletResponse) response; //кастинг сервлетов

        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            String sessionId = request.getSession().getId();
            User user = securityContext.getUserBySessionId(sessionId);
            if (user == null) {
                try {
                    String[] credentials = getCredentials(request.getHeader("Authorization"));
                    UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(UserNotFoundException::new);
                    if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
                        throw new UserNotFoundException();
                    }
                    user = new User(userAccount.getLogin(), userAccount.getRoles());
                    securityContext.addSessionId(sessionId, user);
                } catch (UserNotFoundException | IllegalArgumentException e) {
                    res.sendError(401, "Unauthorized");
                    return;
                }
            }

            request = new WrappedRequest(request, user.getName(), user.getRoles());
        }

        chain.doFilter(request, response);
    }

    private boolean checkEndPoint(String method, String path) {
        return !(HttpMethod.POST.matches(method) && path.equals("/account/register"));
    }


    private String[] getCredentials(String header) {
        String token = header.split(" ")[1];
        String decode = new String(Base64.getDecoder().decode(token));
        return decode.split(":");
    }

    private class WrappedRequest extends HttpServletRequestWrapper {
        private String login;
        private Set<Roles> roles;

        public WrappedRequest(HttpServletRequest request, String login, Set<Roles> roles) {
            super(request);
            this.login = login;
            this.roles = roles;
        }

        public Principal getUserPrincipal() {
                return new User(login, roles);
        }
    }
}
