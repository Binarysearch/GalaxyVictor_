package com.galaxyvictor.util;

import java.sql.SQLException;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.fleets.FinishTravelDbResponse;
import com.galaxyvictor.servlet.fleets.RemoveFleetDTO;
import com.galaxyvictor.servlet.fleets.Travel;
import com.galaxyvictor.servlet.fleets.TravelList;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;
import com.google.gson.Gson;

public class GvFutureEventService implements FutureEventService {

    private DatabaseService databaseService;
    private FutureEventManager eventManager = new FutureEventManager();
    private MessagingService messagingService;

    public GvFutureEventService(DatabaseService databaseService, MessagingService messagingService) {
        this.databaseService = databaseService;
        this.messagingService = messagingService;
        eventManager.start();
		try {
			String result = databaseService.executeQueryForJson("select core.get_travels();");
			TravelList list = new Gson().fromJson(result, TravelList.class);
			if(list != null && list.getTravels() != null){
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
		eventManager.addFutureEvent(new FutureEvent() {

			@Override
			public double getEndTime() {
				double speed = travel.getSpeed();
				double x = travel.getX1() - travel.getX0();
				double y = travel.getY1() - travel.getY0();
				double travelDistance = Math.sqrt(x * x + y * y);
				double travelTime = travelDistance / speed;
				return travel.getStartTime() + travelTime;
			}

			@Override
			public void finish() {
				try {
					String result = databaseService.executeQueryForJson("select core.finish_travel(?);", travel.getFleet());
					FinishTravelDbResponse dbResponse = new Gson().fromJson(result, FinishTravelDbResponse.class);
					messagingService.sendMessageToCivilization(travel.getCivilization(), new Message("FinishTravel", dbResponse));
					
					if (dbResponse.getResultingFleet() != null) {
						//send remove incoming fleet and update resulting fleet to dest civilizations
						if (dbResponse.getDestinationCivilizations() != null) {
							for (long civId : dbResponse.getDestinationCivilizations()) {
								messagingService.sendMessageToCivilization(civId, new Message("RemoveFleet", new RemoveFleetDTO(dbResponse.getIncomingFleetId())));
								messagingService.sendMessageToCivilization(civId, new Message("Fleet", dbResponse.getResultingFleet()));
							}
						}
					}
					//FinishTravelDbResponse dbResponse = new Gson().fromJson(result, FinishTravelDbResponse.class);

					//ExploringResultDTO exploringResultDTO = new Gson().fromJson(result, ExploringResultDTO.class);
				
					//messagingService.sendMessageToCivilization(travel.getCivilization(), new Message("ExploringResult", exploringResultDTO));

					//VisibilityGainedDTO payload = new VisibilityGainedDTO(fleets, colonies, new ArrayList<>(civilizations.values()));
        	//messagingService.sendMessageToCivilization(civilization.getId(), new Message("VisibilityGained", payload));

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

}