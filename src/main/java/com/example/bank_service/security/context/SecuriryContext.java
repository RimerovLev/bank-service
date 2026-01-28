package com.example.bank_service.security.context;

import com.example.bank_service.security.model.User;

public interface SecuriryContext {
    User addSessionId (String sessionId, User user);
    User removeSessionId (String sessionId);
    User getUserBySessionId (String sessionId);


}
