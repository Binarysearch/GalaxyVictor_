package com.galaxyvictor.controllers;

import com.galaxyvictor.websocket.Response;

public class BadLoginResponse implements Response {
    
    private String result = "Incorrect username or password";

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }
}