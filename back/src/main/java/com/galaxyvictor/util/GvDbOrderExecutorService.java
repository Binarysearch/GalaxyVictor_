package com.galaxyvictor.util;

import java.util.List;

import com.galaxyvictor.servlet.DbResponse;
import com.galaxyvictor.servlet.MessageOrder;
import com.galaxyvictor.websocket.Message;
import com.galaxyvictor.websocket.MessagingService;

public class GvDbOrderExecutorService implements DbOrderExecutorService {

    private MessagingService messagingService;
    private FutureEventService futureEventService;

    public GvDbOrderExecutorService(MessagingService mgs, FutureEventService fes) {
        this.messagingService = mgs;
        this.futureEventService = fes;
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

        List<DbCreatedAsincTask> asincTasks = dbResponse.getAsincTasks();
        if (asincTasks != null) {
            for (DbCreatedAsincTask asincTask : asincTasks) {
                futureEventService.addAsincTask(asincTask);
            }
        }

    }

}