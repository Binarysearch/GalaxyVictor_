package com.galaxyvictor.servlet.civilization;


public class CivilizationDTO {

    private long id;
    private String name;
    private long homeworld;

    public CivilizationDTO(){}

    public CivilizationDTO(long id, String name, long homeworld){
        this.id = id;
        this.name = name;
        this.homeworld = homeworld;
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
     * @return the homeworld
     */
    public long getHomeworld() {
        return homeworld;
    }

    /**
     * @param homeworld the homeworld to set
     */
    public void setHomeworld(long homeworld) {
        this.homeworld = homeworld;
    }
}