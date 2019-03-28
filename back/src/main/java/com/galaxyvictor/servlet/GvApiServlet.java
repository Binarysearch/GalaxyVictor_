package com.galaxyvictor.servlet;

import java.sql.SQLException;
import java.util.List;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.util.FutureEventService;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;
import com.google.gson.Gson;

public abstract class GvApiServlet extends ApiServlet {

    private static final long serialVersionUID = 2633133399452838448L;
    private MessagingService messagingService;
    private FutureEventService futureEventService;

    public GvApiServlet() {
        this.messagingService = ServiceManager.get(MessagingService.class);
        this.futureEventService = ServiceManager.get(FutureEventService.class);
    }

    @Override
    protected String postRequest(ApiRequest request) throws SQLException {
        GvApiRequest gv = getPostApiRequest(request);
        if (gv == null) {
            return super.postRequest(request);
        }

        DbResponse dbResponse = executeQueryForObject(buildSql(gv), DbResponse.class, gv.getDbParams());

        List<MessageOrder> messageOrders = dbResponse.getMessageOrders();
        if (messageOrders != null) {
            for (MessageOrder messageOrder : messageOrders) {
                Message message = new Message(messageOrder.getType(), messageOrder.getPayload());
                for (long civilization : messageOrder.getCivilizations()) {
                    messagingService.sendMessageToCivilization(civilization, message);
                }
            }
        }

        List<Long> asincTaskCancelOrders = dbResponse.getAsincTaskCancelOrders();
        if (asincTaskCancelOrders != null) {
            for (long id : asincTaskCancelOrders) {
                futureEventService.cancelAsincTask(id);
            }
        }

        List<AsincTaskOrder> asincTaskOrders = dbResponse.getAsincTaskOrders();
        if (asincTaskOrders != null) {
            for (AsincTaskOrder asincTaskOrder : asincTaskOrders) {
                futureEventService.executeAsincTaskOrder(asincTaskOrder);
            }
        }



        Object apiResponse = dbResponse.getApiResponse();
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