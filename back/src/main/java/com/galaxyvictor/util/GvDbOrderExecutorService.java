package com.galaxyvictor.util;

import java.util.List;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.AsincTaskOrder;
import com.galaxyvictor.servlet.DbResponse;
import com.galaxyvictor.servlet.MessageOrder;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;

public class GvDbOrderExecutorService implements DbOrderExecutorService {

    private MessagingService messagingService;
    private FutureEventService futureEventService;
    private DatabaseService databaseService;

    public GvDbOrderExecutorService(MessagingService mgs, FutureEventService fes, DatabaseService dbs) {
        this.messagingService = mgs;
        this.futureEventService = fes;
        this.databaseService = dbs;
    }

    @Override
    public void executeDbOrder(DbResponse dbResponse) {
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
        
    }

}