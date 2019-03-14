package com.galaxyvictor.servlet;

public class HttpException extends RuntimeException {

	private static final long serialVersionUID = -6320056618756960548L;
    private final int statusCode;

    public HttpException(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }


}