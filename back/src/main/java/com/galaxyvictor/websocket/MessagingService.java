package com.galaxyvictor.websocket;


public interface MessagingService {

    public void onConnectionCreated(GvWebSocket webSocket);
    public void onConnectionClosed(GvWebSocket webSocket);

}