package com.galaxyvictor.servlet.civilization;

import com.galaxyvictor.servlet.galaxies.PlanetDTO;

public class UserCivilizationDTO {

	private long id;
    private String name;
    private long serverTime = System.currentTimeMillis();
    private PlanetDTO homeworld;

    public UserCivilizationDTO() {
    }

    /**
     * @return the homeworld
     */
    public PlanetDTO getHomeworld() {
        return homeworld;
    }

    /**
     * @param homeworld the homeworld to set
     */
    public void setHomeworld(PlanetDTO homeworld) {
        this.homeworld = homeworld;
    }

    /**
     * @return the serverTime
     */
    public long getServerTime() {
        return serverTime;
    }

    /**
     * @param serverTime the serverTime to set
     */
    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
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

}