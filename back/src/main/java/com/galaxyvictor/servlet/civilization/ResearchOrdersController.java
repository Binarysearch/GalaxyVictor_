package com.galaxyvictor.servlet.civilization;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.GvApiRequest;
import com.galaxyvictor.servlet.GvApiServlet;

@WebServlet(urlPatterns = "/api/research-orders")
public class ResearchOrdersController extends GvApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	protected GvApiRequest getPostApiRequest(ApiRequest request) {
		String token = request.getToken();
		int starSystem = request.jsonPath("$.starSystem");
		String technology = request.jsonPath("$.technology");
		long time = System.currentTimeMillis();

		return new GvApiRequest("core.set_research_order", starSystem, technology, time, token);
	}

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		String result = executeQueryForJson("select core.get_civilization_research_orders(?);", token);

		return result;
	}
}