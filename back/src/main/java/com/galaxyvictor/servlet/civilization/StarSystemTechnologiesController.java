package com.galaxyvictor.servlet.civilization;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;

@WebServlet(urlPatterns = "/api/star-system-technologies")
public class StarSystemTechnologiesController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		long starSystem = request.getLongParam("starSystem", 0);
		String result = executeQueryForJson("select core.get_star_system_technologies(?, ?);", starSystem, token);

		return result;
	}
}