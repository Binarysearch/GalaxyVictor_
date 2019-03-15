package com.galaxyvictor.servlet.galaxies;


public class PlanetDTO {

	private long id;
    private long starSystem;
    private int orbit;
    private int type;
    private int size;

    public PlanetDTO() {}

    public PlanetDTO(long id, long starSystem, int orbit, int type, int size) {
        this.id = id;
        this.starSystem = starSystem;
        this.orbit = orbit;
        this.type = type;
        this.size = size;
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
     * @return the orbit
     */
    public int getOrbit() {
        return orbit;
    }

    /**
     * @param orbit the orbit to set
     */
    public void setOrbit(int orbit) {
        this.orbit = orbit;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

}