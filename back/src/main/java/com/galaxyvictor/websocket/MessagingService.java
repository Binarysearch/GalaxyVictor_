package com.galaxyvictor.websocket;


public interface MessagingService {

    public void onConnectionCreated(WebSocket webSocket);
    public void onConnectionClosed(WebSocket webSocket);
    public void sendMessageToCivilization(long civilizationId, Message message);

}