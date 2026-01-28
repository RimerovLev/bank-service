package com.example.bank_service.security.context;

import com.example.bank_service.security.model.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class SecurityContextImpl implements SecuriryContext{
    private Map<String, User> context = new ConcurrentHashMap<>();

    @Override
    public User addSessionId(String sessionId, User user) {
        return context.putIfAbsent(sessionId, user);
    }

    @Override
    public User removeSessionId(String sessionId) {
        return context.remove(sessionId);
    }

    @Override
    public User getUserBySessionId(String sessionId) {
        return context.get(sessionId);
    }
}
