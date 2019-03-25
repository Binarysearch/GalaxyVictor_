package com.galaxyvictor.util;

import java.util.List;

import com.galaxyvictor.servlet.civilization.ColonyBuildingOrder;
import com.galaxyvictor.servlet.fleets.Travel;

public interface FutureEventService {
    
    public void addTravelEvent(Travel travel);

    public void cancelColonyBuildingEvent(long colonyId);

    public void addColonyBuildingEvent(ColonyBuildingOrder order);

	public List<FutureEvent> getEvents();

}