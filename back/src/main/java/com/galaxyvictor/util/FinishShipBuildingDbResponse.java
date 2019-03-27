package com.galaxyvictor.util;

import java.util.List;

import com.galaxyvictor.servlet.fleets.FleetDTO;

public class FinishShipBuildingDbResponse {

    private long colony;
    private FleetDTO fleet;
    private List<Long> civilizations;

    /**
     * @return the colony
     */
    public long getColony() {
        return colony;
    }

    /**
     * @param colony the colony to set
     */
    public void setColony(long colony) {
        this.colony = colony;
    }

    /**
     * @return the fleet
     */
    public FleetDTO getFleet() {
        return fleet;
    }

    /**
     * @param fleet the fleet to set
     */
    public void setFleet(FleetDTO fleet) {
        this.fleet = fleet;
    }

    /**
     * @return the civilizations
     */
    public List<Long> getCivilizations() {
        return civilizations;
    }

    /**
     * @param civilizations the civilizations to set
     */
    public void setCivilizations(List<Long> civilizations) {
        this.civilizations = civilizations;
    }
    
}