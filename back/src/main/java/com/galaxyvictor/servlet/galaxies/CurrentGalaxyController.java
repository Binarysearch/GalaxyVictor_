package com.galaxyvictor.servlet.galaxies;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;

@WebServlet(urlPatterns = "/api/current-galaxy")
public class CurrentGalaxyController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String putRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		int id = request.jsonPath("$.id");
		String result = executeQueryForJson("select core.set_current_galaxy(?, ?);", id, token);

		return result;
	}
}