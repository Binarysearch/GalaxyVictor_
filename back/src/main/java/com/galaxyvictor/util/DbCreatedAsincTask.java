package com.galaxyvictor.util;


public class DbCreatedAsincTask extends FutureEvent{

    private long id;
    private double endTime;

    @Override
    public double getEndTime() {
        return endTime;
    }

    @Override
    public void finish() {
        
    }

    @Override
    public long getId() {
        return id;
    }

}