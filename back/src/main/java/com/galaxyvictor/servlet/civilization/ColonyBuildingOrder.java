package com.galaxyvictor.servlet.civilization;

import java.util.List;

public class ColonyBuildingOrder {

    private String buildingTypeId;
    private String name;
    private long colony;
    private long civilization;
    private long startedTime;
    private List<ColonyBuildingTypeCostDTO> costs;
    private List<ColonyResourceDTO> colonyResources;

    /**
     * @return the colonyResources
     */
    public List<ColonyResourceDTO> getColonyResources() {
        return colonyResources;
    }

    /**
     * @return the buildingTypeId
     */
    public String getBuildingTypeId() {
        return buildingTypeId;
    }

    /**
     * @param buildingTypeId the buildingTypeId to set
     */
    public void setBuildingTypeId(String buildingTypeId) {
        this.buildingTypeId = buildingTypeId;
    }

    /**
     * @param colonyResources the colonyResources to set
     */
    public void setColonyResources(List<ColonyResourceDTO> colonyResources) {
        this.colonyResources = colonyResources;
    }

    /**
     * @return the costs
     */
    public List<ColonyBuildingTypeCostDTO> getCosts() {
        return costs;
    }

    /**
     * @param costs the costs to set
     */
    public void setCosts(List<ColonyBuildingTypeCostDTO> costs) {
        this.costs = costs;
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

}