package com.galaxyvictor.servlet.fleets;

import java.util.List;

import com.galaxyvictor.servlet.civilization.CivilizationDTO;

public class StartTravelDbResponse {

    private List<Long> destinationCivilizations;
    private List<Long> originCivilizations;
    private FleetDTO fleet;
    private CivilizationDTO civilization;
    private Travel travel;

    /**
     * @return the destinationCivilizations
     */
    public List<Long> getDestinationCivilizations() {
        return destinationCivilizations;
    }

    /**
     * @return the civilization
     */
    public CivilizationDTO getCivilization() {
        return civilization;
    }

    /**
     * @param civilization the civilization to set
     */
    public void setCivilization(CivilizationDTO civilization) {
        this.civilization = civilization;
    }

    /**
     * @return the travel
     */
    public Travel getTravel() {
        return travel;
    }

    /**
     * @param travel the travel to set
     */
    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    /**
     * @param destinationCivilizations the destinationCivilizations to set
     */
    public void setDestinationCivilizations(List<Long> destinationCivilizations) {
        this.destinationCivilizations = destinationCivilizations;
    }

    /**
     * @return the originCivilizations
     */
    public List<Long> getOriginCivilizations() {
        return originCivilizations;
    }

    /**
     * @param originCivilizations the originCivilizations to set
     */
    public void setOriginCivilizations(List<Long> originCivilizations) {
        this.originCivilizations = originCivilizations;
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

}