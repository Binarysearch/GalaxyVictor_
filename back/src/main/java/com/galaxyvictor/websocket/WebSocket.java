package com.galaxyvictor.websocket;

import java.io.IOException;

import javax.websocket.Session;

public interface WebSocket {

    public Session getSession();
    public long getCivilizationId();
	public void send(String message)  throws IOException;
    
}