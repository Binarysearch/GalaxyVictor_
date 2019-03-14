package com.galaxyvictor.servlet.auth;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;

@WebServlet(urlPatterns = "/api/login")
public class LoginController extends ApiServlet {

		private static final long serialVersionUID = -2952931963712964636L;

		@Override
		public String postRequest(ApiRequest request) {
				return request.getRequestBody();
		}
}