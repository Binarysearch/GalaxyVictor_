package com.galaxyvictor.controllers;

import com.galaxyvictor.websocket.Response;

public class LolResponse implements Response {

    private String lol;

    public LolResponse(String lol) {
        this.lol = lol;
    }

    /**
     * @return the lol
     */
    public String getLol() {
        return lol;
    }

    /**
     * @param lol the lol to set
     */
    public void setLol(String lol) {
        this.lol = lol;
    }

}