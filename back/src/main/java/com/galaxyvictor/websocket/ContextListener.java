package com.galaxyvictor.websocket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.galaxyvictor.controllers.LoginController;
import com.galaxyvictor.controllers.LolController;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AuthService authService = new GvAuthService();

        RequestDispatcher dispatcher = new GvRequestDispatcher(authService);

        dispatcher.addRequestController("lol", new LolController());

        dispatcher.addRequestController("Login", new LoginController(authService));

        BeanManager.addBean(RequestDispatcher.class, dispatcher);
        BeanManager.addBean(MessagingService.class, new GvMessagingService());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}