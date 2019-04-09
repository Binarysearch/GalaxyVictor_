package com.galaxyvictor.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;

public class ApiRequest {

    private final HttpServletRequest originalRequest;
    private String body;
    private String paramsAsJson;

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
     * @return the requestParams
     */
    public String getRequestParamsAsJson() {
        if (paramsAsJson == null) {
            Map<String, Object> params = new HashMap<>();
            for (String key : originalRequest.getParameterMap().keySet()) {
                String value = originalRequest.getParameter(key);
                if (isNumeric(value)){
                    params.put(key, Double.parseDouble(value));
                } else if (isBoolean(value)){
                    params.put(key, Boolean.parseBoolean(value));
                } else{
                    params.put(key, value);
                }
            }
            paramsAsJson = new Gson().toJson(params);
        }
        return paramsAsJson;
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

	public long getLongParam(String name, long defaultValue) {
        String value = originalRequest.getParameter(name);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (Exception e) {}
        }
		return defaultValue;
	}

	public String getPathInfo() {
		return originalRequest.getPathInfo();
    }
    
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isBoolean(String str) {
        return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false");
    }

}