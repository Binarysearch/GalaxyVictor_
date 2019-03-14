package com.galaxyvictor.websocket;


public class GvMessagingService implements MessagingService{

	private static GvMessagingService instance;

    public static GvMessagingService getInstance() {
        if (instance == null) {
            instance = new GvMessagingService();
        }
		return instance;
	}

	@Override
	public void onConnectionCreated(GvWebSocket webSocket) {

	}

	@Override
	public void onConnectionClosed(GvWebSocket webSocket) {

	}
    
}