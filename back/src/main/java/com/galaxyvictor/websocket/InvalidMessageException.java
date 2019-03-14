package com.galaxyvictor.websocket;

public class InvalidMessageException extends IllegalArgumentException {

    private static final long serialVersionUID = -210275367251165341L;

    public InvalidMessageException(Exception e) {
        super(e);
    }
}