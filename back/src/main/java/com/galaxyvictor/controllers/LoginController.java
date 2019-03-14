package com.galaxyvictor.controllers;

import com.galaxyvictor.websocket.AuthService;
import com.galaxyvictor.websocket.Request;
import com.galaxyvictor.websocket.RequestController;
import com.galaxyvictor.websocket.Response;
import com.galaxyvictor.websocket.User;
import com.jayway.jsonpath.JsonPath;

public class LoginController implements RequestController {

    private AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
	}

	@Override
    public Response computeResponse(Request request) {
        String email = JsonPath.read(request.getMessage(), "$.payload.email");
        String password = JsonPath.read(request.getMessage(), "$.payload.password");

        if (email.equals("email") && password.equals("12345")) {
            authService.authenticate(request.getWebSocket(), new User());
            return new LoginResponse(email);
        }

        return new BadLoginResponse();
    }

}