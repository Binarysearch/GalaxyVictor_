package com.galaxyvictor.websocket;

import java.util.HashMap;

public class GvAuthService implements AuthService {

    private HashMap<String, User> sessions = new HashMap<>();

    public GvAuthService(){

    }

    @Override
    public User getUser(WebSocket webSocket) {
        return sessions.get(webSocket.getSession().getId());
    }

    @Override
    public void authenticate(WebSocket webSocket, User user) {
        sessions.put(webSocket.getSession().getId(), user);
    }

}