package com.galaxyvictor.servlet;

import java.sql.SQLException;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.util.DbOrderExecutorService;
import com.google.gson.Gson;

public abstract class GvApiServlet extends ApiServlet {

    private static final long serialVersionUID = 2633133399452838448L;
    private DbOrderExecutorService dbOrderExecutor;

    public GvApiServlet() {
        this.dbOrderExecutor = ServiceManager.get(DbOrderExecutorService.class);
    }

    @Override
    protected String postRequest(ApiRequest request) throws SQLException {
        GvApiRequest gv = getPostApiRequest(request);
        if (gv == null) {
            return super.postRequest(request);
        }

        DbResponse dbOrder = executeQueryForObject(buildSql(gv), DbResponse.class, gv.getDbParams());

        dbOrderExecutor.executeDbOrder(dbOrder);

        Object apiResponse = dbOrder.getApiResponse();
        if (apiResponse != null) {
            return new Gson().toJson(apiResponse);
        } else {
            return "{}";
        }
    }

    private String buildSql(GvApiRequest gv) {
        StringBuilder sb = new StringBuilder();
        sb.append("select " + gv.getProcedureName() + "(");

        if (gv.getDbParams() != null && gv.getDbParams().length > 0) {
            for (int i = 0; i<gv.getDbParams().length; i++) {
                sb.append("?,");
            }
    
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append(");");
        return sb.toString();
    }

    protected abstract GvApiRequest getPostApiRequest(ApiRequest request);

    
}