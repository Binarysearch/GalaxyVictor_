package com.galaxyvictor.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonStructure;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.db.DatabaseService;

public abstract class ApiServlet extends HttpServlet {

    private static final long serialVersionUID = -2952931963712964636L;
    private DatabaseService databaseService;

    public ApiServlet() {
        this.databaseService = ServiceManager.get(DatabaseService.class);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "content-type, token");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setCharacterEncoding("Utf-8");
        try {
            ApiRequest request = new ApiRequest(req);
            response.getWriter().print(postRequest(request));
        } catch (Exception e) {
            handleException(e, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setCharacterEncoding("Utf-8");
        try {
            ApiRequest request = new ApiRequest(req);
            response.getWriter().print(getRequest(request));
        } catch (Exception e) {
            handleException(e, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            ApiRequest request = new ApiRequest(req);
            response.getWriter().print(putRequest(request));
        } catch (Exception e) {
            handleException(e, response);
        }
    }

    private void handleException(Exception e, HttpServletResponse response) throws IOException {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            response.setStatus(httpException.getStatusCode());
            String message = e.getMessage();
            if (message != null) {
                response.getWriter().print(message);
            }
        } else if (e instanceof SQLException) {
            SQLException sqlException = (SQLException) e;
            handleSQLException(sqlException, response);
        } else {
            response.setStatus(500);
            e.printStackTrace(response.getWriter());
        }
    }

    private void handleSQLException(SQLException e, HttpServletResponse response) throws IOException{
        String ss = e.getSQLState();
        if (ss.startsWith("GV")) {
            response.setStatus(Integer.parseInt(ss.substring(2)));
            String data = e.getMessage().split("  Where: ")[0].split("ERROR: ")[1];
            String where = e.getMessage().split("  Where: ")[1];
            try{
                where = where
                .split(" at PERFORM")[0]
                .split("\n")[2]
                .split("\\.\"")[1].replace("(json)", "").replace("\"", "");
            }catch(Exception e2){
                try{
                    where = where
                    .split(" at PERFORM")[0]
                    .split("\n")[2];
                }catch(Exception e3){

                }
            }
            JsonStructure dataObject = Json.createReader(new StringReader(data)).readObject();

            response.getWriter().append(
                Json.createObjectBuilder()
                    .add("error", dataObject)
                    .add("where", where)
                    .add("code", Integer.parseInt(ss.substring(2)))
                    .build()
                    .toString()
            );
        }else {
            response.setStatus(500);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            response.getWriter().append(
                Json.createObjectBuilder()
                    .add("error", Json.createObjectBuilder().add("message", e.getMessage()))
                    .add("where", stackTrace)
                    .add("code", 500)
                    .build()
                    .toString()
            );
        }
    }

    protected String postRequest(ApiRequest request) throws SQLException{
        throw new MethodNotAllowedException();
    }

    protected String getRequest(ApiRequest request) throws SQLException{
        throw new MethodNotAllowedException();
    }

    protected String putRequest(ApiRequest request) throws SQLException{
        throw new MethodNotAllowedException();
    }

    protected Connection getDbConnection() {
        return databaseService.getConnection();
    }

    protected String executeQueryForJson(String sql, Object... params) throws SQLException {
        return databaseService.executeQueryForJson(sql, params);
    }

    protected <T> T executeQueryForObject(String sql, Class<? extends T> c, Object... params) throws SQLException {
        return databaseService.executeQueryForObject(sql, c, params);
    }

}