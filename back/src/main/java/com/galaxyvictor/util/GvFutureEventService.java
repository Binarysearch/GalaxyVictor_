package com.galaxyvictor.util;

import java.sql.SQLException;
import java.util.List;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.civilization.ColonyBuildingOrder;
import com.galaxyvictor.servlet.civilization.ColonyBuildingOrderList;
import com.galaxyvictor.servlet.fleets.Travel;
import com.galaxyvictor.servlet.fleets.TravelList;
import com.galaxyvictor.websocket.MessagingService;

public class GvFutureEventService implements FutureEventService {

	private DatabaseService databaseService;
	private FutureEventManager eventManager = new FutureEventManager();
	private MessagingService messagingService;

	public GvFutureEventService(DatabaseService databaseService, MessagingService messagingService) {
		this.databaseService = databaseService;
		this.messagingService = messagingService;
		eventManager.start();
		try {

			// Add travels to queue
			TravelList travelList = databaseService.executeQueryForObject("select core.get_travels();", TravelList.class);
			if (travelList != null && travelList.getTravels() != null) {
				for (Travel travel : travelList.getTravels()) {
					addTravelEvent(travel);
				}
			}

			// Add colony buildings to queue
			ColonyBuildingOrderList orderList = databaseService.executeQueryForObject("select core.get_colony_building_orders();", ColonyBuildingOrderList.class);
			if (orderList != null && orderList.getOrders() != null) {
				for (ColonyBuildingOrder order : orderList.getOrders()) {
					addColonyBuildingEvent(order);
				}
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addTravelEvent(Travel travel) {
		eventManager.addFutureEvent(new TravelAsincTask(travel, databaseService, messagingService));
	}

	@Override
	public List<FutureEvent> getEvents() {
		return eventManager.getEvents();
	}

	@Override
	public void cancelColonyBuildingEvent(long colonyId) {
		eventManager.removeEvent(colonyId);
	}

	@Override
	public void addColonyBuildingEvent(ColonyBuildingOrder order) {
		eventManager.addFutureEvent(new ColonyBuildingAsincTask(order, databaseService, messagingService));
	}

}