package com.galaxyvictor.servlet.fleets;

import java.sql.Array;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;

@WebServlet(urlPatterns = "/api/fleets")
public class FleetsController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;

	private TravelsService travelsService;

	private MessagingService messagingService;

	public FleetsController(){
		this.travelsService = ServiceManager.get(TravelsService.class);
		this.messagingService = ServiceManager.get(MessagingService.class);
		
	}

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		String result = executeQueryForJson("select core.get_fleets(?);", token);

		return result;
	}

	@Override
	public String postRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();

		List<Integer> ships = request.jsonPath("ships");
		int fleet = request.jsonPath("fleet");
		int destination = request.jsonPath("destination");
		long time = System.currentTimeMillis();

		Array shipIds = getDbConnection().createArrayOf("bigint", ships.toArray(new Integer[ships.size()]));

		SplitFleetDbResponse dbResponse = executeQueryForObject("select core.split_fleet(?, ?, ?, ?, ?);", SplitFleetDbResponse.class, fleet, shipIds, destination, time, token);


		travelsService.reportTravelStarted(dbResponse.getStartTravelDbResponse());

		//send 'update fleet' to origin civilizations
		if(dbResponse.getStartTravelDbResponse().getOriginCivilizations() != null){
			for (long civId : dbResponse.getStartTravelDbResponse().getOriginCivilizations()) {
				messagingService.sendMessageToCivilization(civId, new Message("Fleet", dbResponse.getStayingFleet()));
			}
		}

		return "{}";
	}


}