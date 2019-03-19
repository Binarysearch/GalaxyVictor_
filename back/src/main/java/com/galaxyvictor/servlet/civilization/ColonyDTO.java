package com.galaxyvictor.servlet.civilization;


public class ColonyDTO{
    
    private long id;
    private long planet;
    private long civilization;

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
     * @return the planet
     */
    public long getPlanet() {
        return planet;
    }

    /**
     * @param planet the planet to set
     */
    public void setPlanet(long planet) {
        this.planet = planet;
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