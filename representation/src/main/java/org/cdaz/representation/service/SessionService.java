package org.cdaz.representation.service;

import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;

public interface SessionService {
    void login(String userId, WebSocketSession session);
    void logout(String userId);
    WebSocketSession getSession(String userId);
}
