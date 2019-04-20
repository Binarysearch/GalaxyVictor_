package com.galaxyvictor.servlet.auth;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;

@WebServlet(urlPatterns = "/api/login")
public class LoginController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String postRequest(ApiRequest request) throws SQLException {
		String email = request.jsonPath("$.email");
		String password = request.jsonPath("$.password");
		long time = System.currentTimeMillis();

		String result = executeQueryForJson("select core.login(?, ?, ?);", email, password, time);

		return result;
	}
}