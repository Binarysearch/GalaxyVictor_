package com.galaxyvictor.servlet;

import java.util.List;

public class MessageOrder {

    private String type;
    private Object payload;
    private List<Long> civilizations;

    /**
     * @return the civilizations
     */
    public List<Long> getCivilizations() {
        return civilizations;
    }

    /**
     * @param civilizations the civilizations to set
     */
    public void setCivilizations(List<Long> civilizations) {
        this.civilizations = civilizations;
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
     * @return the payload
     */
    public Object getPayload() {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(Object payload) {
        this.payload = payload;
    }
    
}