package com.galaxyvictor.websocket;


public interface AuthService {

	User getUser(WebSocket webSocket);

	void authenticate(WebSocket webSocket, User user);

}