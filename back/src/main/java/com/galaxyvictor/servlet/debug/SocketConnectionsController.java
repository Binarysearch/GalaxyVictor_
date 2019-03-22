package com.galaxyvictor.servlet.debug;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;
import com.galaxyvictor.websocket.MessagingService;
import com.galaxyvictor.websocket.WebSocket;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/api/socket-connections")
public class SocketConnectionsController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;
	private MessagingService messagingService;

	public SocketConnectionsController(){
		this.messagingService = ServiceManager.get(MessagingService.class);
	}

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		executeQueryForJson("select core.auth(?);", token);

		Map<Long, Map<String, WebSocket>> connections = messagingService.getConnections();

		List<SocketConnectionListDTO> result = connections.entrySet().stream().map((e) -> {
			return new SocketConnectionListDTO(e.getKey(), e.getValue().keySet());
		}).collect(Collectors.toList());
		

		return new Gson().toJson(result);
	}
}