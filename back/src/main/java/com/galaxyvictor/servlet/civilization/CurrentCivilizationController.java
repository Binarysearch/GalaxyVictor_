package com.galaxyvictor.servlet.civilization;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/api/civilization")
public class CurrentCivilizationController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		String result = executeQueryForJson("select core.get_current_civilization(?);", token);

		UserCivilizationDTO dto = new Gson().fromJson(result, UserCivilizationDTO.class);
		dto.setServerTime(System.currentTimeMillis());
		return new Gson().toJson(dto);
	}
}