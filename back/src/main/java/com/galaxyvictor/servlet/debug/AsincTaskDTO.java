package com.galaxyvictor.servlet.debug;


public class AsincTaskDTO {

    private String type;
    private double remainingTime;

    public AsincTaskDTO() {
    }

    public AsincTaskDTO(String type, double remainingTime){
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