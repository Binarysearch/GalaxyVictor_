package com.galaxyvictor.servlet;

import java.util.List;

import com.galaxyvictor.util.DbCreatedAsincTask;

public class DbResponse {

    private Object apiResponse;
    private List<MessageOrder> messageOrders;
    private List<DbCreatedAsincTask> asincTasks;
    private List<Long> asincTaskCancelOrders;
    private DbResponseError error;

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

    /**
     * @return the error
     */
    public DbResponseError getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(DbResponseError error) {
        this.error = error;
    }

	public boolean hasError() {
		return error != null;
	}

}