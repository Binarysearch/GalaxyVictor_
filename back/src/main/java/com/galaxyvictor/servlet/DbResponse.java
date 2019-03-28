package com.galaxyvictor.servlet;

import java.util.List;

import com.galaxyvictor.util.DbCreatedAsincTask;

public class DbResponse {

    private Object apiResponse;
    private List<MessageOrder> messageOrders;
    private List<AsincTaskOrder> asincTaskOrders;
    private List<DbCreatedAsincTask> asincTasks;
    private List<Long> asincTaskCancelOrders;

    /**
     * @return the apiResponse
     */
    public Object getApiResponse() {
        return apiResponse;
    }

    /**
     * @return the asincTasks
     */
    public List<DbCreatedAsincTask> getAsincTasks() {
        return asincTasks;
    }

    /**
     * @param asincTasks the asincTasks to set
     */
    public void setAsincTasks(List<DbCreatedAsincTask> asincTasks) {
        this.asincTasks = asincTasks;
    }

    /**
     * @param apiResponse the apiResponse to set
     */
    public void setApiResponse(Object apiResponse) {
        this.apiResponse = apiResponse;
    }

    /**
     * @return the messageOrders
     */
    public List<MessageOrder> getMessageOrders() {
        return messageOrders;
    }

    /**
     * @param messageOrders the messageOrders to set
     */
    public void setMessageOrders(List<MessageOrder> messageOrders) {
        this.messageOrders = messageOrders;
    }

    /**
     * @return the asincTaskOrders
     */
    public List<AsincTaskOrder> getAsincTaskOrders() {
        return asincTaskOrders;
    }

    /**
     * @param asincTaskOrders the asincTaskOrders to set
     */
    public void setAsincTaskOrders(List<AsincTaskOrder> asincTaskOrders) {
        this.asincTaskOrders = asincTaskOrders;
    }

    /**
     * @return the asincTaskCancelOrders
     */
    public List<Long> getAsincTaskCancelOrders() {
        return asincTaskCancelOrders;
    }

    /**
     * @param asincTaskCancelOrders the asincTaskCancelOrders to set
     */
    public void setAsincTaskCancelOrders(List<Long> asincTaskCancelOrders) {
        this.asincTaskCancelOrders = asincTaskCancelOrders;
    }

}