package com.galaxyvictor.util;

import java.util.List;

import com.galaxyvictor.servlet.fleets.Travel;

public interface FutureEventService {
    
    public void addTravelEvent(Travel travel);

	public List<FutureEvent> getEvents();

}