package com.galaxyvictor.servlet.fleets;


public class RemoveFleetDTO {

    private long id;

    public RemoveFleetDTO() {}
    
    public RemoveFleetDTO(long id) {
        this.setId(id);
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