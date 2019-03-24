package com.galaxyvictor.servlet.civilization;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;

@WebServlet(urlPatterns = "/api/colony-resources")
public class ColonyResourcesController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		int colony = request.jsonPath("$.colony");
		String result = executeQueryForJson("select core.get_colony_resources(?, ?);", colony, token);

		return result;
	}
}