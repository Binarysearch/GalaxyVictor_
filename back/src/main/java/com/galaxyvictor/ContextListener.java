package com.galaxyvictor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.galaxyvictor.auth.AuthService;
import com.galaxyvictor.auth.GvAuthService;
import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.db.DbData;
import com.galaxyvictor.db.GvDatabaseService;
import com.galaxyvictor.servlet.fleets.TravelsService;
import com.galaxyvictor.util.DbOrderExecutorService;
import com.galaxyvictor.util.FutureEventService;
import com.galaxyvictor.util.GvDbOrderExecutorService;
import com.galaxyvictor.util.GvFutureEventService;
import com.galaxyvictor.websocket.GvMessagingService;
import com.galaxyvictor.websocket.MessagingService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@WebListener
public class ContextListener implements ServletContextListener {

    public static final boolean DROP_AND_CREATE_DATABASE_SCHEMA_ON_STARTUP = true;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseService dbs = new GvDatabaseService(DbData.getConnectionData());
        MessagingService mgs = new GvMessagingService();

        if (DROP_AND_CREATE_DATABASE_SCHEMA_ON_STARTUP) {
            try {
                
                dropAndCreateDbSchema(dbs);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        FutureEventService fes = new GvFutureEventService(dbs, mgs);
        
        ServiceManager.addService(DatabaseService.class, dbs);
        ServiceManager.addService(MessagingService.class, mgs);
        ServiceManager.addService(AuthService.class, new GvAuthService(dbs));
        ServiceManager.addService(FutureEventService.class, fes);
        ServiceManager.addService(TravelsService.class, new TravelsService(mgs, fes, dbs));
        ServiceManager.addService(DbOrderExecutorService.class, new GvDbOrderExecutorService(mgs, fes));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void dropAndCreateDbSchema(DatabaseService dbs) {
        System.out.println();
        System.out.println("CREATING DATABASE SCHEMA....");
        System.out.println();

        String schema = getResourceAsString("/db/schema.sql");
        String installScript = getResourceAsString("/db/install.sql");
        
        dbs.executeSql(schema);
        
        createDbProcedures(dbs);
        createDbTriggers(dbs);
        
        System.out.println("\nCREATING INITIAL DATA\n");
        dbs.executeSql(installScript);

        System.out.println();
        System.out.println("DATABASE SCHEMA CREATED");
        System.out.println();
    }

    private void createDbProcedures(DatabaseService dbs) {
        System.out.println("\nCREATING PROCEDURES\n");
        String procedureList = getResourceAsString("/db/procedures/list.json");
        List<String> list = new Gson().fromJson(procedureList, new TypeToken<ArrayList<String>>(){}.getType());
        for (String fileName : list) {
            String procedure = getResourceAsString("/db/procedures/" + fileName);
            System.out.println("CREATING PROCEDURE: '" + fileName + "' ...");
            dbs.executeSql(procedure);
        }
    }

    private void createDbTriggers(DatabaseService dbs) {
        System.out.println("\nCREATING TRIGGERS\n");
        String triggerList = getResourceAsString("/db/triggers/list.json");
        List<String> list = new Gson().fromJson(triggerList, new TypeToken<ArrayList<String>>(){}.getType());
        for (String fileName : list) {
            String trigger = getResourceAsString("/db/triggers/" + fileName);
            System.out.println("CREATING TRIGGER: '" + fileName + "' ...");
            dbs.executeSql(trigger);
        }
    }

    private String getResourceAsString(String path) {
        InputStream in = getClass().getResourceAsStream("../.." + path);
        String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        return result;
    }

    
}