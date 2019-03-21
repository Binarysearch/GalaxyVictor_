package com.galaxyvictor.servlet.fleets;


public class SplitFleetDbResponse {

    private StartTravelDbResponse startTravelDbResponse;
    private FleetDTO stayingFleet;

    /**
     * @return the startTravelDbResponse
     */
    public StartTravelDbResponse getStartTravelDbResponse() {
        return startTravelDbResponse;
    }

    /**
     * @param startTravelDbResponse the startTravelDbResponse to set
     */
    public void setStartTravelDbResponse(StartTravelDbResponse startTravelDbResponse) {
        this.startTravelDbResponse = startTravelDbResponse;
    }

    /**
     * @return the stayingFleet
     */
    public FleetDTO getStayingFleet() {
        return stayingFleet;
    }

    /**
     * @param stayingFleet the stayingFleet to set
     */
    public void setStayingFleet(FleetDTO stayingFleet) {
        this.stayingFleet = stayingFleet;
    }
    
}