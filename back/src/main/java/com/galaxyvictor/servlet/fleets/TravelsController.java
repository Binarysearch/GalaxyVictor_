package com.galaxyvictor.servlet.fleets;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;

@WebServlet(urlPatterns = "/api/travels")
public class TravelsController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;
	
	private TravelsService travelsService;


	public TravelsController(){
		this.travelsService = ServiceManager.get(TravelsService.class);
		
	}

	@Override
	public String postRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		int fleet = request.jsonPath("$.fleet");
		int destination = request.jsonPath("$.destination");
		long time = System.currentTimeMillis();

		travelsService.startTravel(fleet, destination, time, token);

		return "{}";
	}



}