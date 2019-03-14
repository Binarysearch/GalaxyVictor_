package com.galaxyvictor.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ApiServlet extends HttpServlet {

    private static final long serialVersionUID = -2952931963712964636L;

    @Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, DELETE");
		response.setHeader("Access-Control-Allow-Headers", "content-type");
	}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            ApiRequest request = new ApiRequest(req);
            response.getWriter().print(postRequest(request));
        } catch (Exception e) {
            handlePostException(e, response);
        }
    }
    
    private void handlePostException(Exception e, HttpServletResponse response) throws IOException {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            response.setStatus(httpException.getStatusCode());
            String message = e.getMessage();
            if (message != null) {
                response.getWriter().print(message);
            }
        } else {
            response.setStatus(500);
            e.printStackTrace(response.getWriter());
        }
    }

    protected String postRequest(ApiRequest request) {
        throw new MethodNotAllowedException();
    }

    
}