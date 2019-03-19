package com.galaxyvictor.servlet.fleets;


public class Travel {

    private long fleet;
    private long civilization;
    private double speed;
    private double x0;
    private double x1;
    private double y0;
    private double y1;
    private long startTime;

    /**
     * @return the speed
     */
    public double getSpeed() {
        return speed;
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

    /**
     * @return the fleet
     */
    public long getFleet() {
        return fleet;
    }

    /**
     * @param fleet the fleet to set
     */
    public void setFleet(long fleet) {
        this.fleet = fleet;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * @return the x0
     */
    public double getX0() {
        return x0;
    }

    /**
     * @param x0 the x0 to set
     */
    public void setX0(double x0) {
        this.x0 = x0;
    }

    /**
     * @return the x1
     */
    public double getX1() {
        return x1;
    }

    /**
     * @param x1 the x1 to set
     */
    public void setX1(double x1) {
        this.x1 = x1;
    }

    /**
     * @return the y0
     */
    public double getY0() {
        return y0;
    }

    /**
     * @param y0 the y0 to set
     */
    public void setY0(double y0) {
        this.y0 = y0;
    }

    /**
     * @return the y1
     */
    public double getY1() {
        return y1;
    }

    /**
     * @param y1 the y1 to set
     */
    public void setY1(double y1) {
        this.y1 = y1;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

}