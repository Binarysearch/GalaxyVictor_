package com.galaxyvictor.servlet.civilization;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/api/civilization")
public class CivilizationController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String postRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		String name = request.jsonPath("$.name");
		String homeStarName = request.jsonPath("$.homeStarName");


		UserCivilizationDTO dto = executeQueryForObject("select core.create_civilization(?, ?, ?);", UserCivilizationDTO.class, name, homeStarName, token);
		dto.setServerTime(System.currentTimeMillis());
		return new Gson().toJson(dto);
	}

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();

		UserCivilizationDTO dto = executeQueryForObject("select core.get_current_civilization(?);", UserCivilizationDTO.class, token);
		dto.setServerTime(System.currentTimeMillis());
		return new Gson().toJson(dto);
	}
}