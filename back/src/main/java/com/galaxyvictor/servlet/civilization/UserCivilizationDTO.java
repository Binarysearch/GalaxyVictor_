package com.galaxyvictor.servlet.civilization;

import com.galaxyvictor.servlet.galaxies.PlanetDTO;

public class UserCivilizationDTO {

	private long id;
    private String name;
    private long serverTime = System.currentTimeMillis();
    private PlanetDTO homeworld;

    private long tradeRoutesCache;
    private long researchOrdersCache;
    private long shipModelsCache;
    private long civilizationsCache;
    private long planetsCache;

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

    /**
     * @return the tradeRoutesCache
     */
    public long getTradeRoutesCache() {
        return tradeRoutesCache;
    }

    /**
     * @param tradeRoutesCache the tradeRoutesCache to set
     */
    public void setTradeRoutesCache(long tradeRoutesCache) {
        this.tradeRoutesCache = tradeRoutesCache;
    }

    /**
     * @return the researchOrdersCache
     */
    public long getResearchOrdersCache() {
        return researchOrdersCache;
    }

    /**
     * @param researchOrdersCache the researchOrdersCache to set
     */
    public void setResearchOrdersCache(long researchOrdersCache) {
        this.researchOrdersCache = researchOrdersCache;
    }

    /**
     * @return the shipModelsCache
     */
    public long getShipModelsCache() {
        return shipModelsCache;
    }

    /**
     * @param shipModelsCache the shipModelsCache to set
     */
    public void setShipModelsCache(long shipModelsCache) {
        this.shipModelsCache = shipModelsCache;
    }

    /**
     * @return the civilizationsCache
     */
    public long getCivilizationsCache() {
        return civilizationsCache;
    }

    /**
     * @param civilizationsCache the civilizationsCache to set
     */
    public void setCivilizationsCache(long civilizationsCache) {
        this.civilizationsCache = civilizationsCache;
    }

    /**
     * @return the planetsCache
     */
    public long getPlanetsCache() {
        return planetsCache;
    }

    /**
     * @param planetsCache the planetsCache to set
     */
    public void setPlanetsCache(long planetsCache) {
        this.planetsCache = planetsCache;
    }
}