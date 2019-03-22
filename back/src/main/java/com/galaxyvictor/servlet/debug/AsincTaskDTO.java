package com.galaxyvictor.servlet.debug;


public class AsincTaskDTO {

    private long id;
    private String type;
    private double remainingTime;

    public AsincTaskDTO() {
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

    public AsincTaskDTO(long id, String type, double remainingTime) {
        this.id = id;
        this.type = type;
        this.remainingTime = remainingTime;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the remainingTime
     */
    public double getRemainingTime() {
        return remainingTime;
    }

    /**
     * @param remainingTime the remainingTime to set
     */
    public void setRemainingTime(double remainingTime) {
        this.remainingTime = remainingTime;
    }

}