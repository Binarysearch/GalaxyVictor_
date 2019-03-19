package com.galaxyvictor.servlet.civilization;



public class VisibilityLostDTO {

	private long starSystem;

    public VisibilityLostDTO() {}

    /**
     * @return the starSystem
     */
    public long getStarSystem() {
        return starSystem;
    }

    /**
     * @param starSystem the starSystem to set
     */
    public void setStarSystem(long starSystem) {
        this.starSystem = starSystem;
    }

    public VisibilityLostDTO(long starSystem) {
        this.setStarSystem(starSystem);
	}
    
}