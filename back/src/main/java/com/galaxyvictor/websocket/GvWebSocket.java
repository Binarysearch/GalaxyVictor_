package com.galaxyvictor.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/socket")
public class GvWebSocket implements WebSocket{

	private final GvRequestDispatcher requestDispatcher;
	private final MessagingService messagingService;
	private Session session;

	public GvWebSocket() {
		this(GvRequestDispatcher.getInstance(), GvMessagingService.getInstance());
	}

	public GvWebSocket(GvRequestDispatcher requestDispatcher, MessagingService messagingService) {
		this.requestDispatcher = requestDispatcher;
		this.messagingService = messagingService;
	}

	@OnOpen
	public void onOpen(Session session){
		this.session = session;
		this.messagingService.onConnectionCreated(this);
	}

	@OnClose
	public void onClose(){
		this.messagingService.onConnectionClosed(this);
	}

	@OnMessage
	public String onMessage(String message){
		return requestDispatcher.dispatch(this, message);
	}

	@OnError
	public void onError(Throwable e){
		this.messagingService.onConnectionClosed(this);
	}

	@Override
	public Session getSession(){
		return this.session;
	}
}