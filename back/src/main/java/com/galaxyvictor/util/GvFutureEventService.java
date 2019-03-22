package com.galaxyvictor.util;

import java.sql.SQLException;
import java.util.List;

import com.galaxyvictor.db.DatabaseService;
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
			TravelList list = databaseService.executeQueryForObject("select core.get_travels();", TravelList.class);
			if (list != null && list.getTravels() != null) {
				for (Travel travel : list.getTravels()) {
					addTravelEvent(travel);
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

}