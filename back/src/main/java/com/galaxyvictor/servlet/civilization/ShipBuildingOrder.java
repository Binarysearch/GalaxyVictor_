package com.galaxyvictor.servlet.civilization;


public class ShipBuildingOrder {

    private long shipModelId;
    private String name;
    private long colony;
    private long civilization;
    private long startedTime;

    /**
     * @return the shipModelId
     */
    public long getShipModelId() {
        return shipModelId;
    }

    /**
     * @param shipModelId the shipModelId to set
     */
    public void setShipModelId(long shipModelId) {
        this.shipModelId = shipModelId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

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
     * @return the startedTime
     */
    public long getStartedTime() {
        return startedTime;
    }

    /**
     * @param startedTime the startedTime to set
     */
    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
    }

}