package com.galaxyvictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        createDbRest(dbs);
        createDbTriggers(dbs);
        createDbTests(dbs);

        System.out.println("\nCREATING INSTALL DATA\n");
        dbs.executeSql(installScript);

        System.out.println();
        System.out.println("DATABASE SCHEMA CREATED");
        System.out.println();

        System.out.println("\n**** RUN TESTS ****\n");
        runDbTests(dbs);
        runRestTests(dbs);
        System.out.println("\n**** TESTS FINISHED ****\n");

        System.out.println("\nCREATING TEST DATA\n");
        dbs.executeSql(initialData);
    }

    private void runRestTests(DatabaseService dbs) {
        // execute tests
        Connection c = null;
        try {
            c = dbs.getConnection();
            ResultSet r = c.createStatement().executeQuery("select id,path,method,name from rest.tests;");
            while (r.next()) {
                System.err.printf("Executing rest test %s. Path:'%s', Method:'%s'...\n", r.getString(4), r.getString(2), r.getString(3));
                dbs.executeSql("select rest.execute_test(?)",r.getLong(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (c != null) {
                try {c.close();} catch (SQLException e) {}
            }
        }

        // show test results
        c = null;
        try {
            c = dbs.getConnection();
            ResultSet r = c.createStatement().executeQuery("select path,method,error_message,error_code,name from rest.tests where not passed;");
            boolean errors = false;
            while (r.next()) {
                errors = true;
                System.err.println("TEST NOT PASSED");
                System.err.printf("Name: '%s'.\n", r.getString(5));
                System.err.printf("Path: '%s', method: '%s'\n", r.getString(1), r.getString(2));
                System.err.printf("Error message: '%s'\n", r.getString(3));
                System.err.printf("Error code: '%s'\n", r.getString(4));
                System.err.println("**********");
            }
            if (errors) {
                throw new RuntimeException("Tests not passed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (c != null) {
                try {c.close();} catch (SQLException e) {}
            }
        }
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

    private void createDbRest(DatabaseService dbs) {
        System.out.println("\nCREATING REST SCHEMA\n");

        String restSchema = getResourceAsString("/db/rest/rest_schema.sql");
        dbs.executeSql(restSchema);

        String validateJson = getResourceAsString("/db/rest/validate_json.sql");
        dbs.executeSql(validateJson);

        // Create get endpoints
        try {
            System.out.println("\nCREATING GET ENDPOINTS\n");
            URL url = getClass().getResource("../../db/rest/endpoints/get");
            if(url != null){
                File dir = new File(url.toURI());
                createRestEndpoint("get", "", dir, dbs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Create post endpoints
        try {
            System.out.println("\nCREATING POST ENDPOINTS\n");
            URL url = getClass().getResource("../../db/rest/endpoints/post");
            if(url != null){
                File dir = new File(url.toURI());
                createRestEndpoint("post", "", dir, dbs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Create put endpoints
        try {
            System.out.println("\nCREATING PUT ENDPOINTS\n");
            URL url = getClass().getResource("../../db/rest/endpoints/put");
            if(url != null){
                File dir = new File(url.toURI());
                createRestEndpoint("put", "", dir, dbs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Create delete endpoints
        try {
            System.out.println("\nCREATING DELETE ENDPOINTS\n");
            URL url = getClass().getResource("../../db/rest/endpoints/delete");
            if(url != null){
                File dir = new File(url.toURI());
                createRestEndpoint("delete", "", dir, dbs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        System.out.println("\nFINISH CREATING REST SCHEMA\n");
    }

    private void createRestEndpoint(String method, String path, File file, DatabaseService dbs) throws Exception{
        if (file.isDirectory()) {
            boolean codeFound = false;
            boolean schemaFound = false;
            for (File child : file.listFiles()) {
                codeFound |= child.getName().equals("code.sql");
                schemaFound |= child.getName().equals("schema.json");
                if (file.isDirectory()) {
                    createRestEndpoint(method, path + "/" + child.getName(), child, dbs);
                }
            }
            if (codeFound) {
                
                String codePath = file.getAbsolutePath() + "\\code.sql";
                String code = new String(Files.readAllBytes(Paths.get(codePath)), StandardCharsets.UTF_8);
                
                String schema = "{}";
                if(schemaFound){
                    String schemaPath = file.getAbsolutePath() + "\\schema.json";
                    schema = new String(Files.readAllBytes(Paths.get(schemaPath)), StandardCharsets.UTF_8);;
                } 
                
                dbs.executeSql("insert into rest.endpoints(path, method, code, schema) values(?,?,?,?::jsonb);", path, method, code, schema);
                createRestEndpointTests(path, method, dbs);
            }
        }
    }

    private void createRestEndpointTests(String path, String method, DatabaseService dbs) {
        try {
            URL url = getClass().getResource("../../db/rest/tests/" + method + path);
            if(url != null){
                File dir = new File(url.toURI());
                if(dir.isDirectory()){
                    for (File file : dir.listFiles()) {
                        if (file.getName().endsWith(".sql")) {
                            String code = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
                            dbs.executeSql("insert into rest.tests(path, method, code, name) values(?,?,?,?);", path, method, code, file.getName().replace(".sql", ""));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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