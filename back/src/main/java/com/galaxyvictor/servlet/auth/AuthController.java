package com.galaxyvictor.servlet.auth;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;

@WebServlet(urlPatterns = "/api/auth")
public class AuthController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String postRequest(ApiRequest request) throws SQLException {
		String token = request.getRequestBody();

		String result = executeQueryForJson("select core.auth(?);", token);

		return result;
	}
}