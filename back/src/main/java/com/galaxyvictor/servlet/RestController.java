package com.galaxyvictor.servlet;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.ApiServlet;
import com.galaxyvictor.util.DbOrderExecutorService;
import com.google.gson.Gson;

@WebServlet(urlPatterns = "/rest/*")
public class RestController extends ApiServlet {

	private static final long serialVersionUID = -2952931963712964636L;
	private DbOrderExecutorService dbOrderExecutor;
	
	public RestController(){
		this.dbOrderExecutor = ServiceManager.get(DbOrderExecutorService.class);
	}

	@Override
	public String getRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		String path = request.getPathInfo();
		String method = "GET";
		String params = request.getRequestParamsAsJson();
		

		DbResponse dbOrder = executeQueryForObject("select rest.execute_api(?, ?, ?, ?::jsonb)", DbResponse.class, path, method, token, params);
		
		if (dbOrder == null) {
			return "{}";
		}
		dbOrderExecutor.executeDbOrder(dbOrder);
        Object apiResponse = dbOrder.getApiResponse();
        if (apiResponse != null) {
            return new Gson().toJson(apiResponse);
        } else {
            return "{}";
        }
	}

	@Override
	public String postRequest(ApiRequest request) throws SQLException {
		String token = request.getToken();
		String path = request.getPathInfo();
		String method = "POST";
		String params = request.getRequestBody();
		

		DbResponse dbOrder = executeQueryForObject("select rest.execute_api(?, ?, ?, ?::jsonb)", DbResponse.class, path, method, token, params);
		
		if (dbOrder == null) {
			return "{}";
		}
		dbOrderExecutor.executeDbOrder(dbOrder);
        Object apiResponse = dbOrder.getApiResponse();
        if (apiResponse != null) {
            return new Gson().toJson(apiResponse);
        } else {
            return "{}";
        }
	}


}