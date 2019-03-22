package com.galaxyvictor.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.Gson;

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

    @Override
    public void executeSql(String sql) {
        Connection db = getConnection();

        try {
            Statement st = db.createStatement();
            st.execute(sql);
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                db.close();
            } catch (SQLException e) {}
        }
    }

    @Override
    public String executeQueryForJson(String sql, Object... params) throws SQLException {
        Connection db = getConnection();
        try {
            PreparedStatement st = db.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }

            ResultSet r = st.executeQuery();
            if (r.next()) {
                return r.getString(1);
            }
        } finally {
            try {
                db.close();
            } catch (SQLException e) {}
        }
        return null;
    }

    @Override
    public <T> T executeQueryForObject(String sql, Class<? extends T> c, Object... params) throws SQLException {
        String result = executeQueryForJson(sql, params);
        return new Gson().fromJson(result, c);
    }
}