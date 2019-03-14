package com.galaxyvictor.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/socket")
public class WebSocket {

	private static final ConcurrentLinkedQueue<WebSocket> connections = new ConcurrentLinkedQueue<>();
	private Session session;
	private static int i = 0;
	private int number;


	@OnOpen
	public void onOpen(Session session){
		this.session = session;
		connections.add(this);
		number = ++i;
		broadcast("Client " + number + " connected.");
	}

	@OnClose
	public void onClose(){
		synchronized (this) {
			connections.remove(this);
		}
	}

	public void broadcast(String message) {
		synchronized (connections) {
			for (WebSocket c : connections) {
				try {
					synchronized (c) {
						c.session.getBasicRemote().sendText(message);
					}
				} catch (IOException e) {
					connections.remove(c);
					try {
						c.session.close();
					} catch (IOException e1) {}
				}
			}
		}
	}

	@OnMessage
	public String onMessage(String message){
		broadcast("Client " + number + ": " + message);
		return null;
	}

	@OnError
	public void onError(Throwable e){
		e.printStackTrace();
	}

}