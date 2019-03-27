package com.galaxyvictor.util;

import java.sql.SQLException;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.civilization.FinishColonyBuildingDTO;
import com.galaxyvictor.servlet.civilization.ShipBuildingOrder;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;

public class ColonyShipBuildingAsincTask extends FutureEvent {

    private ShipBuildingOrder order;
    private DatabaseService databaseService;
    private MessagingService messagingService;

    public ColonyShipBuildingAsincTask(ShipBuildingOrder order, DatabaseService databaseService,
            MessagingService messagingService) {
        this.order = order;
        this.databaseService = databaseService;
        this.messagingService = messagingService;
    }

    @Override
    public double getEndTime() {
        return order.getStartedTime() + 10000;
    }

    @Override
    public void finish() {
        try {
            FinishShipBuildingDbResponse dbResponse = this.databaseService.executeQueryForObject(
                    "select core.finish_colony_ship_order(?);", FinishShipBuildingDbResponse.class, order.getColony());
            
            this.messagingService.sendMessageToCivilization(order.getCivilization(), new Message("FinishColonyBuilding", new FinishColonyBuildingDTO(order.getColony())));

            if (dbResponse.getFleet() != null) {
                //send remove fleet and update fleet to civilizations
                if (dbResponse.getCivilizations() != null) {
                    for (long civId : dbResponse.getCivilizations()) {
                        messagingService.sendMessageToCivilization(civId, new Message("Fleet", dbResponse.getFleet()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getId() {
        return order.getColony();
    }

}