package com.galaxyvictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        String initialData = getResourceAsString("/db/initial_data.sql");
        
        dbs.executeSql(schema);
        
        createDbProcedures(dbs);
        createDbTriggers(dbs);
        createDbTests(dbs);
        
        System.out.println("\nCREATING INSTALL DATA\n");
        dbs.executeSql(installScript);

        System.out.println();
        System.out.println("DATABASE SCHEMA CREATED");
        System.out.println();

        

        System.out.println("\n**** RUN TESTS ****\n");
        runDbTests(dbs);
        System.out.println("\n**** TESTS FINISHED ****\n");

        
        System.out.println("\nCREATING TEST DATA\n");
        dbs.executeSql(initialData);
    }

    private void runDbTests(DatabaseService dbs) {
        String testPlan = getResourceAsString("/db/tests/test_plan.sql");
        dbs.executeSql(testPlan);
    }

    private void createDbTests(DatabaseService dbs) {        
        System.out.println("\nCREATING TESTS\n");

        executeSqlFromDir(dbs, "/db/tests/dto", "DTO TEST");
        executeSqlFromDir(dbs, "/db/tests/proc", "TEST");
        
        System.out.println("\nFINISH CREATING TESTS\n");
    }

    private void createDbProcedures(DatabaseService dbs) {
        System.out.println("\nCREATING PROCEDURES\n");
        executeSqlFromDir(dbs, "/db/procedures", "PROCEDURE");
        System.out.println("\nFINISH CREATING PROCEDURES\n");
    }

    private void createDbTriggers(DatabaseService dbs) {
        System.out.println("\nCREATING TRIGGERS\n");
        executeSqlFromDir(dbs, "/db/triggers", "TRIGGER");
        System.out.println("\nFINISH CREATING TRIGGERS\n");
    }

    private void executeSqlFromDir(DatabaseService dbs, String path, String logTypeWord){
        try {
            URL url = getClass().getResource("../.." + path);
            File dir = new File(url.toURI());
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    continue;
                }
                String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
                System.out.println("CREATING " + logTypeWord + ": '" + file.getName() + "' ...");
                dbs.executeSql(content);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getResourceAsString(String path) {
        InputStream in = getClass().getResourceAsStream("../.." + path);
        String result = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        return result;
    }

    
}