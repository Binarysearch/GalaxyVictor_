package com.galaxyvictor.servlet.fleets;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;
import com.galaxyvictor.servlet.civilization.VisibilityLostDTO;
import com.galaxyvictor.util.FutureEventService;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/api/travels")
public class TravelsController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;
	
	private MessagingService messagingService;
	private FutureEventService futureEventService;


	public TravelsController(){
		this.messagingService = ServiceManager.get(MessagingService.class);
		this.futureEventService = ServiceManager.get(FutureEventService.class);
		
	}

	@Override
	public String postRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		int fleet = request.jsonPath("$.fleet");
		int destination = request.jsonPath("$.destination");
		long time = System.currentTimeMillis();
		String result = executeQueryForJson("select core.start_travel(?, ?, ?, ?);", fleet, destination, time, token);

		Gson gson = new Gson();
		StartTravelDbResponse dbResponse = gson.fromJson(result, StartTravelDbResponse.class);

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
					messagingService.sendMessageToCivilization(civId, new Message("RemoveFleet", new RemoveFleetDTO(fleet)));
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

		return "{}";
	}



}