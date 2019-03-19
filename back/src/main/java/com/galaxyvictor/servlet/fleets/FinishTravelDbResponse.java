package com.galaxyvictor.servlet.fleets;

import java.util.List;

import com.galaxyvictor.servlet.civilization.CivilizationDTO;
import com.galaxyvictor.servlet.civilization.ColonyDTO;
import com.galaxyvictor.servlet.galaxies.PlanetDTO;

public class FinishTravelDbResponse {

    private List<PlanetDTO> planets;
    private List<FleetDTO> fleets;
    private List<ColonyDTO> colonies;
    private List<CivilizationDTO> civilizations;

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

    /**
     * @return the fleets
     */
    public List<FleetDTO> getFleets() {
        return fleets;
    }

    /**
     * @param fleets the fleets to set
     */
    public void setFleets(List<FleetDTO> fleets) {
        this.fleets = fleets;
    }

    /**
     * @return the colonies
     */
    public List<ColonyDTO> getColonies() {
        return colonies;
    }

    /**
     * @param colonies the colonies to set
     */
    public void setColonies(List<ColonyDTO> colonies) {
        this.colonies = colonies;
    }

    /**
     * @return the civilizations
     */
    public List<CivilizationDTO> getCivilizations() {
        return civilizations;
    }

    /**
     * @param civilizations the civilizations to set
     */
    public void setCivilizations(List<CivilizationDTO> civilizations) {
        this.civilizations = civilizations;
    }

}