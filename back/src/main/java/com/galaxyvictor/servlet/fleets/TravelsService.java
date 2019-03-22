package com.galaxyvictor.servlet.fleets;

import java.sql.SQLException;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.civilization.VisibilityLostDTO;
import com.galaxyvictor.util.FutureEventService;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;

public class TravelsService {

    private MessagingService messagingService;
    private FutureEventService futureEventService;
    private DatabaseService databaseService;

    public TravelsService(MessagingService messagingService, FutureEventService futureEventService,
            DatabaseService databaseService) {
        this.messagingService = messagingService;
        this.futureEventService = futureEventService;
        this.databaseService = databaseService;
    }

    public void startTravel(long fleet, long destination, long time, String token) throws SQLException {
        StartTravelDbResponse dbResponse = databaseService.executeQueryForObject("select core.start_travel(?, ?, ?, ?);", StartTravelDbResponse.class, fleet, destination, time, token);
        reportTravelStarted(dbResponse);
    }

    public void reportTravelStarted(StartTravelDbResponse dbResponse){
        Message fleetMessage = new Message("Fleet", dbResponse.getFleet());
		Message civilizationMessage = new Message("Civilization", dbResponse.getCivilization());

		

		boolean visibilityLost = true;

		//send 'remove fleet' to origin civilizations
		if(dbResponse.getOriginCivilizations() != null){
			for (long civId : dbResponse.getOriginCivilizations()) {
				//if civilization still in origin star system dont send visibility lost message
				if (civId == dbResponse.getCivilization().getId()) {
					visibilityLost = false;
				} else {
					messagingService.sendMessageToCivilization(civId, new Message("RemoveFleet", new RemoveFleetDTO(dbResponse.getFleet().getId())));
				}
				
			}
		}

		//send 'update fleet' to destination civilizations
		//send 'new civ' to destination civilizations
		if(dbResponse.getDestinationCivilizations() != null){
			for (long civId : dbResponse.getDestinationCivilizations()) {
				if (civId != dbResponse.getCivilization().getId()) {
					messagingService.sendMessageToCivilization(civId, civilizationMessage);
				}
				messagingService.sendMessageToCivilization(civId, fleetMessage);
			}
		}

		//send visibilityLost message
		if (visibilityLost) {
			VisibilityLostDTO payload = new VisibilityLostDTO(dbResponse.getFleet().getOrigin());
        	messagingService.sendMessageToCivilization(dbResponse.getCivilization().getId(), new Message("VisibilityLost", payload));
		}

		messagingService.sendMessageToCivilization(dbResponse.getFleet().getCivilization(), fleetMessage);

		futureEventService.addTravelEvent(dbResponse.getTravel());
    }
}