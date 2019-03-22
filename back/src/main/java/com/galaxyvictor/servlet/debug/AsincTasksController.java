package com.galaxyvictor.servlet.debug;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;
import com.galaxyvictor.util.FutureEvent;
import com.galaxyvictor.util.FutureEventService;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/api/asinc-tasks")
public class AsincTasksController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;
	private FutureEventService futureEventService;

	public AsincTasksController(){
		this.futureEventService = ServiceManager.get(FutureEventService.class);
	}

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		executeQueryForJson("select core.auth(?);", token);

		List<FutureEvent> events = futureEventService.getEvents();
		
		List<AsincTaskDTO> tasks = events.stream().map((e) -> {
			return new AsincTaskDTO(e.getId(), e.getClass().getSimpleName(), e.getRemainingTime());
		}).collect(Collectors.toList());

		return new Gson().toJson(tasks);
	}
}