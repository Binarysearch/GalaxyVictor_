package com.galaxyvictor.servlet.fleets;

import java.util.List;

import com.galaxyvictor.servlet.galaxies.PlanetDTO;

public class ExploringResultDTO {

    private long starSystem;
    private List<PlanetDTO> planets;

    public ExploringResultDTO() {
    }
    
	public ExploringResultDTO(long starSystem, List<PlanetDTO> planets) {
        this.starSystem = starSystem;
        this.planets = planets;
	}

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

    /**
     * @return the planets
     */
    public List<PlanetDTO> getPlanets() {
        return planets;
    }

    /**
     * @param planets the planets to set
     */
    public void setPlanets(List<PlanetDTO> planets) {
        this.planets = planets;
    }

}