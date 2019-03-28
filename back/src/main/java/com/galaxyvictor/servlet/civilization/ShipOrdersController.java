package com.galaxyvictor.servlet.civilization;


import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.GvApiRequest;
import com.galaxyvictor.servlet.GvApiServlet;

@WebServlet(urlPatterns = "/api/ship-orders")
public class ShipOrdersController extends GvApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	@Override
	protected GvApiRequest getPostApiRequest(ApiRequest request) {
		String token = request.getToken();
		int colony = request.jsonPath("$.colony");
		int shipModel = request.jsonPath("$.shipModel");
		long time = System.currentTimeMillis();

		return new GvApiRequest("core.set_colony_ship_order", colony, shipModel, time, token);
	}
}