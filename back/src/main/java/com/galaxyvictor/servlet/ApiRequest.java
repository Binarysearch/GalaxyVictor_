package com.galaxyvictor.servlet;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.jayway.jsonpath.JsonPath;

public class ApiRequest {

    private final HttpServletRequest originalRequest;
    private String body;

    public ApiRequest(HttpServletRequest originalRequest) {
        this.originalRequest = originalRequest;
    }

    /**
     * @return the requestBody
     */
    public String getRequestBody() {
        if (body == null) {
            try {
                body = originalRequest.getReader().lines().collect(Collectors.joining());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return body;
    }

    /**
     * @return the originalRequest
     */
    public HttpServletRequest getOriginalRequest() {
        return originalRequest;
    }

    @SuppressWarnings("unchecked")
    public <T> T jsonPath(String path) {
        return (T) JsonPath.read(getRequestBody(), path);
    }

    public String getToken() {
        String token = originalRequest.getHeader("token");
        if(token != null){
            return token;
        }
        return "";
    }
}