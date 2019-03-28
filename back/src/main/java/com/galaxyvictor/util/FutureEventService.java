package com.galaxyvictor.util;

import java.util.List;

import com.galaxyvictor.servlet.AsincTaskOrder;
import com.galaxyvictor.servlet.civilization.ColonyBuildingOrder;
import com.galaxyvictor.servlet.civilization.ShipBuildingOrder;
import com.galaxyvictor.servlet.fleets.Travel;

public interface FutureEventService {
    
    public void addTravelEvent(Travel travel);

    public void cancelColonyBuildingEvent(long colonyId);

    public void addColonyBuildingEvent(ColonyBuildingOrder order);

	public List<FutureEvent> getEvents();

	public void addColonyShipBuildingEvent(ShipBuildingOrder order);

	public void executeAsincTaskOrder(AsincTaskOrder asincTaskOrder);

    public void cancelAsincTask(long id);
    
    public void registerAsincTaskBuilder(AsincTaskBuilder builder);

	public void addAsincTask(DbCreatedAsincTask asincTask);

}