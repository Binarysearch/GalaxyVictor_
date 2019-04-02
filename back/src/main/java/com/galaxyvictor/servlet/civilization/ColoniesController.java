package com.galaxyvictor.servlet.civilization;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.GvApiRequest;
import com.galaxyvictor.servlet.GvApiServlet;

@WebServlet(urlPatterns = "/api/colonies")
public class ColoniesController extends GvApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		String result = executeQueryForJson("select core.get_colonies(?);", token);

		return result;
	}

	@Override
	protected GvApiRequest getPostApiRequest(ApiRequest request) {
		String token = request.getToken();
		int planet = request.jsonPath("$.planet");

		return new GvApiRequest("core.colonize_planet", planet, token);
	}
}