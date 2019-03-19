package com.galaxyvictor.servlet.fleets;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;

@WebServlet(urlPatterns = "/api/ships")
public class ShipsController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		long fleet = request.getLongParam("fleet", 0);
		String result = executeQueryForJson("select core.get_ships(?, ?);", fleet, token);

		return result;
	}
}