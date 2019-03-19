package com.galaxyvictor.websocket;


public class Message {


    private String type;
    private Object payload;

    public Message() {
    }

    public Message(String type, Object payload){
        this.type = type;
        this.payload = payload;
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