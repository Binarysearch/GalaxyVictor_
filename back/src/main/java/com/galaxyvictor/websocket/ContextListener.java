package com.galaxyvictor.websocket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.db.GvDatabaseService;


@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseService dbs = new GvDatabaseService();
        BeanManager.addBean(DatabaseService.class, dbs);
        BeanManager.addBean(MessagingService.class, new GvMessagingService());
        BeanManager.addBean(AuthService.class, new GvAuthService());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}