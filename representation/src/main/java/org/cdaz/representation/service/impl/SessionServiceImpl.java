package org.cdaz.representation.service.impl;

import org.cdaz.representation.service.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionServiceImpl implements SessionService {
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void login(String userId, WebSocketSession session) {
        sessionMap.computeIfAbsent(userId, k -> session);
    }

    @Override
    public void logout(String userId) {
        sessionMap.computeIfPresent(userId, (k, v) -> {
            if (v.isOpen()) {
                try {
                    v.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            return v;
        });
        sessionMap.remove(userId);
    }

    @Override
    public WebSocketSession getSession(String userId) {
        return sessionMap.get(userId);
    }
}
