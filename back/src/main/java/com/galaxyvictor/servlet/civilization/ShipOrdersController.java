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

@WebServlet(urlPatterns = "/api/ship-orders")
public class ShipOrdersController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;
	private MessagingService messagingService;
	private FutureEventService futureEventsService;

	public ShipOrdersController(){
		this.messagingService = ServiceManager.get(MessagingService.class);
		this.futureEventsService = ServiceManager.get(FutureEventService.class);
	}

	@Override
	public String postRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		int colony = request.jsonPath("$.colony");
		int shipModel = request.jsonPath("$.shipModel");
		long time = System.currentTimeMillis();

		String response = executeQueryForJson("select core.set_colony_ship_order(?, ?, ?, ?);", colony, shipModel, time, token);
		ShipBuildingOrder order = new Gson().fromJson(response, ShipBuildingOrder.class);

		reportBuildingStarted(order);

		return response;
	}

	private void reportBuildingStarted(ShipBuildingOrder order) {

		messagingService.sendMessageToCivilization(order.getCivilization(), new Message("ShipBuildingOrder", order));
		futureEventsService.cancelColonyBuildingEvent(order.getColony());
		futureEventsService.addColonyShipBuildingEvent(order);

	}


}