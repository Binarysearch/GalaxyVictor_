package com.galaxyvictor.util;

import java.sql.SQLException;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.civilization.ColonyBuildingOrder;
import com.galaxyvictor.servlet.civilization.FinishColonyBuildingDTO;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;

public class ColonyBuildingAsincTask extends FutureEvent {

    private ColonyBuildingOrder order;
    private DatabaseService databaseService;
    private MessagingService messagingService;

    public ColonyBuildingAsincTask(ColonyBuildingOrder order, DatabaseService databaseService,
            MessagingService messagingService) {
        this.order = order;
        this.databaseService = databaseService;
        this.messagingService = messagingService;
    }

    @Override
    public double getEndTime() {
        return order.getStartedTime() + 100000;
    }

    @Override
    public void finish() {
        try {
            this.databaseService.executeQueryForJson(
                    "select core.finish_colony_building_order(?);", order.getColony());
            
            this.messagingService.sendMessageToCivilization(order.getCivilization(), new Message("FinishColonyBuilding", new FinishColonyBuildingDTO(order.getColony())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getId() {
        return order.getColony();
    }

}