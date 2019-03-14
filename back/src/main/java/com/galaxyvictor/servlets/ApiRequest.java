package com.galaxyvictor.servlets;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

public class ApiRequest {

    private final HttpServletRequest originalRequest;

    public ApiRequest(HttpServletRequest originalRequest) {
        this.originalRequest = originalRequest;
    }

    /**
     * @return the requestBody
     */
    public String getRequestBody() {
        try {
            return originalRequest.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the originalRequest
     */
    public HttpServletRequest getOriginalRequest() {
        return originalRequest;
    }

}