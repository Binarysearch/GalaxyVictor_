package com.galaxyvictor.servlet.civilization;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;

@WebServlet(urlPatterns = "/api/colony-buildings")
public class ColonyBuildingsController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		long colony = request.getLongParam("colony", 0);
		String result = executeQueryForJson("select core.get_colony_buildings(?, ?);", colony, token);

		return result;
	}

	@Override
	public String postRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		int colony = request.jsonPath("$.colony");
		String type = request.jsonPath("$.buildingType");
		long time = System.currentTimeMillis();
		String result = executeQueryForJson("select core.set_colony_building_order(?, ?, ?, ?);", colony, type, time, token);

		return result;
	}


}