package com.galaxyvictor.servlet.civilization;


import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.GvApiRequest;
import com.galaxyvictor.servlet.GvApiServlet;
import com.galaxyvictor.servlet.civilization.ShipBuildingOrder;
import com.galaxyvictor.util.AsincTaskBuilder;
import com.galaxyvictor.util.ColonyShipBuildingAsincTask;
import com.galaxyvictor.util.FutureEvent;
import com.galaxyvictor.util.FutureEventService;
import com.galaxyvictor.websocket.MessagingService;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/api/ship-orders")
public class ShipOrdersController extends GvApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	public ShipOrdersController() {
		FutureEventService futureEventsService = ServiceManager.get(FutureEventService.class);
		futureEventsService.registerAsincTaskBuilder(new AsincTaskBuilder(){
		
			@Override
			public String getAsincTaskType() {
				return "BUILD_SHIP";
			}
		
			@Override
			public FutureEvent build(Object asincTaskData, DatabaseService databaseService, MessagingService messagingService) {
				String dataJson = new Gson().toJson(asincTaskData);
				ShipBuildingOrder shipBuildingOrder = new Gson().fromJson(dataJson, ShipBuildingOrder.class);
				return new ColonyShipBuildingAsincTask(shipBuildingOrder, databaseService, messagingService);
			}
		});
	}

	@Override
	protected GvApiRequest getPostApiRequest(ApiRequest request) {
		String token = request.getToken();
		int colony = request.jsonPath("$.colony");
		int shipModel = request.jsonPath("$.shipModel");
		long time = System.currentTimeMillis();

		return new GvApiRequest("core.set_colony_ship_order", colony, shipModel, time, token);
	}


}