package com.galaxyvictor.servlet.fleets;


public class FleetDTO {

    private long id;
    private long civilization;
    private long destination;
    private long origin;
    private long travelStartTime;
    private boolean canColonize;

    public FleetDTO() {
    }

    /**
     * @return the canColonize
     */
    public boolean isCanColonize() {
        return canColonize;
    }

    /**
     * @param canColonize the canColonize to set
     */
    public void setCanColonize(boolean canColonize) {
        this.canColonize = canColonize;
    }

    public FleetDTO(long id, long civilization, long destination, long origin, long travelStartTime) {
        this.id = id;
        this.civilization = civilization;
        this.destination = destination;
        this.origin = origin;
        this.travelStartTime = travelStartTime;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the civilization
     */
    public long getCivilization() {
        return civilization;
    }

    /**
     * @param civilization the civilization to set
     */
    public void setCivilization(long civilization) {
        this.civilization = civilization;
    }

    /**
     * @return the destination
     */
    public long getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(long destination) {
        this.destination = destination;
    }

    /**
     * @return the origin
     */
    public long getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(long origin) {
        this.origin = origin;
    }

    /**
     * @return the travelStartTime
     */
    public long getTravelStartTime() {
        return travelStartTime;
    }

    /**
     * @param travelStartTime the travelStartTime to set
     */
    public void setTravelStartTime(long travelStartTime) {
        this.travelStartTime = travelStartTime;
    }
}