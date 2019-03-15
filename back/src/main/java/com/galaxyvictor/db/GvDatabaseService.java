package com.galaxyvictor.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GvDatabaseService implements DatabaseService {

    private DatabaseConnectionData dbData;

    public GvDatabaseService(DatabaseConnectionData dbData) {
        this.dbData = dbData;
    }

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(dbData.getUrl() + dbData.getDatabaseName(), dbData.getUsername(),
                    dbData.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}