package com.galaxyvictor.util;

import java.sql.SQLException;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.fleets.FinishTravelDbResponse;
import com.galaxyvictor.servlet.fleets.RemoveFleetDTO;
import com.galaxyvictor.servlet.fleets.Travel;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;

public class TravelAsincTask extends FutureEvent {

    private Travel travel;
    private DatabaseService databaseService;
    private MessagingService messagingService;

    public TravelAsincTask(Travel travel, DatabaseService databaseService, MessagingService messagingService) {
        this.travel = travel;
        this.databaseService = databaseService;
        this.messagingService = messagingService;
    }

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
            FinishTravelDbResponse dbResponse = databaseService.executeQueryForObject("select core.finish_travel(?);", FinishTravelDbResponse.class, travel.getFleet());
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getId() {
        return travel.getFleet();
    }

}