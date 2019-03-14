package com.galaxyvictor.websocket;


public interface Request {

	public int getId();

	public String getMessage();
	
	public User getUser();

	public WebSocket getWebSocket();
	
}