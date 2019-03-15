package com.galaxyvictor.auth;

import java.sql.Connection;

import com.galaxyvictor.db.DatabaseService;

public class GvAuthService implements AuthService {

    private final DatabaseService dbService;

    public GvAuthService(DatabaseService dbService) {
        this.dbService = dbService;
    }

    @Override
    public long authenticate(String token) {
        Connection db = dbService.getConnection();

        

        return 0;
    }

}