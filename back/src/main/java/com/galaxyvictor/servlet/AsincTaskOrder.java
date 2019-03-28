package com.galaxyvictor.servlet;


public class AsincTaskOrder {

    private String type;
    private Object asincTaskData;

    /**
     * @return the asincTaskData
     */
    public Object getAsincTaskData() {
        return asincTaskData;
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
     * @param asincTaskData the asincTaskData to set
     */
    public void setAsincTaskData(Object asincTaskData) {
        this.asincTaskData = asincTaskData;
    }

}