package com.galaxyvictor.servlet.civilization;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;
import com.galaxyvictor.util.FutureEventService;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/api/colony-buildings")
public class ColonyBuildingsController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;
	private MessagingService messagingService;
	private FutureEventService futureEventsService;

	public ColonyBuildingsController(){
		this.messagingService = ServiceManager.get(MessagingService.class);
		this.futureEventsService = ServiceManager.get(FutureEventService.class);
	}

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

		String response = executeQueryForJson("select core.set_colony_building_order(?, ?, ?, ?);", colony, type, time, token);
		ColonyBuildingOrder order = new Gson().fromJson(response, ColonyBuildingOrder.class);

		reportBuildingStarted(order);

		return response;
	}

	private void reportBuildingStarted(ColonyBuildingOrder order) {

		messagingService.sendMessageToCivilization(order.getCivilization(), new Message("ColonyBuildingOrder", order));
		futureEventsService.cancelColonyBuildingEvent(order.getColony());
		futureEventsService.addColonyBuildingEvent(order);

	}


}