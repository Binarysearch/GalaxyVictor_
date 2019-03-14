package com.galaxyvictor.servlets;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/api/login")
public class TestEndpoint extends ApiServlet {

    private static final long serialVersionUID = -2952931963712964636L;

    @Override
    protected String postRequest(ApiRequest request) {
        return request.getRequestBody();
    }

    

    
}