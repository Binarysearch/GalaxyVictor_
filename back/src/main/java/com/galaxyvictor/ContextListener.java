package com.galaxyvictor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.galaxyvictor.auth.AuthService;
import com.galaxyvictor.auth.GvAuthService;
import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.db.DbData;
import com.galaxyvictor.db.GvDatabaseService;
import com.galaxyvictor.websocket.GvMessagingService;
import com.galaxyvictor.websocket.MessagingService;


@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseService dbs = new GvDatabaseService(DbData.getConnectionData());
        ServiceManager.addService(DatabaseService.class, dbs);
        ServiceManager.addService(MessagingService.class, new GvMessagingService());
        ServiceManager.addService(AuthService.class, new GvAuthService(dbs));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    
}