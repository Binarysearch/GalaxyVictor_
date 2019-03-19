package com.galaxyvictor.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseService {

    public Connection getConnection();

	public void executeSql(String sql);
    
    public String executeQueryForJson(String sql, Object... params) throws SQLException;
    
}