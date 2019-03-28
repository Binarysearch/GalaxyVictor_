package com.galaxyvictor.util;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.websocket.MessagingService;

public interface AsincTaskBuilder {

	public FutureEvent build(Object asincTaskData, DatabaseService databaseService, MessagingService messagingService);

	public String getAsincTaskType();

}