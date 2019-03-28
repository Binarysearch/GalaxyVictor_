package com.galaxyvictor.util;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.AsincTaskOrder;
import com.galaxyvictor.servlet.civilization.ColonyBuildingOrder;
import com.galaxyvictor.servlet.civilization.ColonyBuildingOrderList;
import com.galaxyvictor.servlet.civilization.ShipBuildingOrder;
import com.galaxyvictor.servlet.civilization.ShipOrderList;
import com.galaxyvictor.servlet.fleets.Travel;
import com.galaxyvictor.servlet.fleets.TravelList;
import com.galaxyvictor.websocket.MessagingService;

public class GvFutureEventService implements FutureEventService {

	private DatabaseService databaseService;
	private FutureEventManager eventManager = new FutureEventManager();
	private MessagingService messagingService;
	private ConcurrentHashMap<String, AsincTaskBuilder> asincTaskBuilders = new ConcurrentHashMap<>();

	public GvFutureEventService(DatabaseService databaseService, MessagingService messagingService) {
		this.databaseService = databaseService;
		this.messagingService = messagingService;
		eventManager.start();
		try {

			// Add travels to queue
			TravelList travelList = databaseService.executeQueryForObject("select core.get_travels();",
					TravelList.class);
			if (travelList != null && travelList.getTravels() != null) {
				for (Travel travel : travelList.getTravels()) {
					addTravelEvent(travel);
				}
			}

			// Add colony buildings to queue
			ColonyBuildingOrderList orderList = databaseService
					.executeQueryForObject("select core.get_colony_building_orders();", ColonyBuildingOrderList.class);
			if (orderList != null && orderList.getOrders() != null) {
				for (ColonyBuildingOrder order : orderList.getOrders()) {
					addColonyBuildingEvent(order);
				}
			}

			// Add ship orders to queue
			ShipOrderList shipOrderList = databaseService.executeQueryForObject("select core.get_colony_ship_orders();",
					ShipOrderList.class);
			if (shipOrderList != null && shipOrderList.getOrders() != null) {
				for (ShipBuildingOrder order : shipOrderList.getOrders()) {
					addColonyShipBuildingEvent(order);
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

	@Override
	public void addColonyShipBuildingEvent(ShipBuildingOrder order) {
		eventManager.addFutureEvent(new ColonyShipBuildingAsincTask(order, databaseService, messagingService));
	}

	@Override
	public void executeAsincTaskOrder(AsincTaskOrder asincTaskOrder) {
		AsincTaskBuilder builder = asincTaskBuilders.get(asincTaskOrder.getType());
		if (builder == null) {
			throw new RuntimeException("AsincTaskBuilder '" + asincTaskOrder.getType() + "' not registered.");
		}

		FutureEvent event = builder.build(asincTaskOrder.getAsincTaskData(), databaseService, messagingService);
		eventManager.addFutureEvent(event);
	}

	@Override
	public void cancelAsincTask(long id) {
		eventManager.removeEvent(id);
	}

	@Override
	public void registerAsincTaskBuilder(AsincTaskBuilder builder) {
		asincTaskBuilders.put(builder.getAsincTaskType(), builder);
	}

}