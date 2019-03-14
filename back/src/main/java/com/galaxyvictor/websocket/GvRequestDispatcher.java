package com.galaxyvictor.websocket;

public class GvRequestDispatcher implements RequestDispatcher{

	private static GvRequestDispatcher instance;

	public static GvRequestDispatcher getInstance() {
		if (instance == null) {
            instance = new GvRequestDispatcher();
        }
		return instance;
	}

	@Override
	public String dispatch(WebSocket webSocket, String message) {
		return message;
	}
    
}