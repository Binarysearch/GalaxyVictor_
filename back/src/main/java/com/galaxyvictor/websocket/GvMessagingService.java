package com.galaxyvictor.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import com.google.gson.Gson;

public class GvMessagingService implements MessagingService {

	private ConcurrentHashMap<Long, ConcurrentHashMap<String, WebSocket>> connections = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Long> reverseConnections = new ConcurrentHashMap<>();
	
	@Override
	public void onConnectionCreated(WebSocket webSocket) {
		Session session = webSocket.getSession();
		addConnection(webSocket.getCivilizationId(), webSocket);
        reverseConnections.put(session.getId(), webSocket.getCivilizationId());
	}

	@Override
	public void onConnectionClosed(WebSocket webSocket) {
		Session session = webSocket.getSession();
		String sessionId = session.getId();
        if (sessionId != null) {
            long civilizationId = reverseConnections.get(sessionId);
            ConcurrentHashMap<String, WebSocket> webSockets = connections.get(civilizationId);
            if (webSockets != null) {
                webSockets.remove(sessionId);
                if (webSockets.isEmpty()) {
                    connections.remove(civilizationId);
                }
            }
            reverseConnections.remove(sessionId);
            System.out.println(connections.size());
            System.out.println(reverseConnections.size());
        }
	}
	
	private void addConnection(long civilizationId, WebSocket webSocket) {
        ConcurrentHashMap<String, WebSocket> webSockets = connections.get(civilizationId);
        if (webSockets == null) {
            webSockets = new ConcurrentHashMap<>();
        }

        webSockets.put(webSocket.getSession().getId(), webSocket);
        connections.put(civilizationId, webSockets);
	}
	
	public void sendMessageToCivilization(long civilizationId, Message message) {
        ConcurrentHashMap<String, WebSocket> webSockets = connections.get(civilizationId);
        if (webSockets != null) {
            Gson gson = new Gson();
            

            String messageString = gson.toJson(message);
            for (WebSocket ws : webSockets.values()) {
                try {
                    ws.send(messageString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}