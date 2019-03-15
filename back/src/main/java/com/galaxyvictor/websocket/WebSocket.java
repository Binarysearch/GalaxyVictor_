package com.galaxyvictor.websocket;

import javax.websocket.Session;

public interface WebSocket {

    public Session getSession();
    public long getCivilizationId();
    
}