package com.galaxyvictor.db;

import java.sql.Connection;

public interface DatabaseService {

    public Connection getConnection();

	public void executeSql(String sql);
    
}