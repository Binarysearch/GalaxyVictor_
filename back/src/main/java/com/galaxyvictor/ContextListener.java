package com.galaxyvictor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

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

    public static final boolean DROP_AND_CREATE_DATABASE_SCHEMA_ON_STARTUP = true;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseService dbs = new GvDatabaseService(DbData.getConnectionData());
        if (DROP_AND_CREATE_DATABASE_SCHEMA_ON_STARTUP) {
            try {
                
                dropAndCreateDbSchema(dbs);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        ServiceManager.addService(DatabaseService.class, dbs);
        ServiceManager.addService(MessagingService.class, new GvMessagingService());
        ServiceManager.addService(AuthService.class, new GvAuthService(dbs));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void dropAndCreateDbSchema(DatabaseService dbs) {
        System.out.println();
        System.out.println("CREATING DATABASE SCHEMA....");
        System.out.println();

        String schema = getResourceAsString("/db/schema.sql");
        String procedures = getResourceAsString("/db/procedures.sql");
        String triggers = getResourceAsString("/db/triggers.sql");
        String installScript = getResourceAsString("/db/install.sql");
        
        dbs.executeSql(schema);
        dbs.executeSql(procedures);
        dbs.executeSql(triggers);
        dbs.executeSql(installScript);

        System.out.println();
        System.out.println("DATABASE SCHEMA CREATED");
        System.out.println();
    }

    private String getResourceAsString(String path) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        return result;
    }

    
}