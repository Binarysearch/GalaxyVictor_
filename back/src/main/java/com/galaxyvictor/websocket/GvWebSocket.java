package com.galaxyvictor.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/socket")
public class GvWebSocket implements WebSocket{

	private final MessagingService messagingService;
	private final AuthService authService;
	private Session session;
	private long civilizationId;

	public GvWebSocket() {
		this(BeanManager.get(MessagingService.class), BeanManager.get(AuthService.class));
	}

	public GvWebSocket(MessagingService messagingService, AuthService authService) {
		this.messagingService = messagingService;
		this.authService = authService;
	}

	@OnOpen
	public void onOpen(Session session){
		this.session = session;
	}

	@OnClose
	public void onClose(){
		this.messagingService.onConnectionClosed(this);
	}

	@OnMessage
	public String onMessage(String message){
		try {
			long civilizationId = authService.authenticate(message);
			if (civilizationId != 0) {
				this.civilizationId = civilizationId;
				this.messagingService.onConnectionCreated(this);
			}
		} catch (Exception e) {
			return e.toString() + e.getMessage();
		}
		return null;
	}

	@OnError
	public void onError(Throwable e){
		this.messagingService.onConnectionClosed(this);
	}

	@Override
	public Session getSession(){
		return this.session;
	}

	@Override
	public long getCivilizationId(){
		return this.civilizationId;
	}


}