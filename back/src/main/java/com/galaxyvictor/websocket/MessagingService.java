package com.galaxyvictor.websocket;

import java.util.Map;

public interface MessagingService {

    public void onConnectionCreated(WebSocket webSocket);
    public void onConnectionClosed(WebSocket webSocket);
    public void sendMessageToCivilization(long civilizationId, Message message);
	public Map<Long, Map<String, WebSocket>> getConnections();

}