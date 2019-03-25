package com.galaxyvictor.servlet.civilization;


public class FinishColonyBuildingDTO {

	private long colony;

    public FinishColonyBuildingDTO(long colony) {
        this.setColony(colony);
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

}