package com.galaxyvictor.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.auth.AuthService;
import com.jayway.jsonpath.JsonPath;

@ServerEndpoint("/socket")
public class GvWebSocket implements WebSocket {

	private final MessagingService messagingService;
	private final AuthService authService;
	private Session session;
	private long civilizationId;

	public GvWebSocket() {
		this(ServiceManager.get(MessagingService.class), ServiceManager.get(AuthService.class));
	}

	public GvWebSocket(MessagingService messagingService, AuthService authService) {
		this.messagingService = messagingService;
		this.authService = authService;
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
	}

	@OnClose
	public void onClose() {
		this.messagingService.onConnectionClosed(this);
	}

	@OnMessage
	public String onMessage(String message) {
		try {
			String type = JsonPath.read(message, "$.type");
			if(type.equals("authToken")){
				String token = JsonPath.read(message, "$.payload");
				long civilizationId = authService.authenticate(token);
				if (civilizationId != 0) {
					this.civilizationId = civilizationId;
					this.messagingService.onConnectionCreated(this);
					return "Conetion created. Civilization id: '" + civilizationId + "'";
				}
			}
		} catch (Exception e) {
			return e.toString() + e.getMessage();
		}
		return "Invalid message";
	}

	@OnError
	public void onError(Throwable e) {
		this.messagingService.onConnectionClosed(this);
	}

	@Override
	public Session getSession() {
		return this.session;
	}

	@Override
	public long getCivilizationId() {
		return this.civilizationId;
	}

	@Override
	public void send(String message) throws IOException {
		getSession().getBasicRemote().sendText(message);
	}


}