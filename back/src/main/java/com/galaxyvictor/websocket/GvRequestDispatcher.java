package com.galaxyvictor.websocket;

import java.util.HashMap;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class GvRequestDispatcher implements RequestDispatcher{

	private HashMap<String, RequestController> controllers = new HashMap<>();

	private AuthService authService;

	public GvRequestDispatcher(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public String dispatch(WebSocket webSocket, String message) {
		String type;
		try {
			type = JsonPath.read(message, "$.type");
		} catch (PathNotFoundException e) {
			throw new InvalidMessageException(e);
		}

		RequestController controller = controllers.get(type);

		if (controller == null) {
			throw new ControllerNotFoundException("Controller for '" + type + "' not found.");
		}

		Request request;

		if (controller.getClass().isAnnotationPresent(RequireLogin.class)){
			request = buildAuthenticatedRequest(webSocket, message);
		} else {
			request = buildRequest(webSocket, message);
		}

		return new Gson().toJson(new IdentifiedResponse(request.getId(), type, controller.computeResponse(request)));
	}

	private Request buildRequest(WebSocket webSocket, String message) {
		int requestId;
		try {
			requestId = JsonPath.read(message, "$.id");
		} catch (PathNotFoundException e) {
			throw new InvalidMessageException(e);
		}

		return new Request(){
		
			@Override
			public int getId() {
				return requestId;
			}

			@Override
			public String getMessage(){
				return message;
			}

			@Override
			public User getUser(){
				return null;
			}

			@Override
			public WebSocket getWebSocket(){
				return webSocket;
			}

		};
	}

	private Request buildAuthenticatedRequest(WebSocket webSocket, String message) {
		int requestId;
		try {
			requestId = JsonPath.read(message, "$.id");
		} catch (PathNotFoundException e) {
			throw new InvalidMessageException(e);
		}

		User user = authService.getUser(webSocket);

		if (user == null) {
			throw new SecurityException("This request: {id: " + requestId + ", type: '" + JsonPath.read(message, "$.type") + "'} requires authentication.");
		}

		return new Request(){
		
			@Override
			public int getId() {
				return requestId;
			}

			@Override
			public String getMessage(){
				return message;
			}

			@Override
			public User getUser(){
				return user;
			}

			@Override
			public WebSocket getWebSocket(){
				return webSocket;
			}
		};
	}

	@Override
	public void addRequestController(String requestType, RequestController controller) {
		controllers.put(requestType, controller);
	}
    
}