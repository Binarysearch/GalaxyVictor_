package com.galaxyvictor.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.galaxyvictor.db.DatabaseService;

public class GvAuthService implements AuthService {

    private final DatabaseService dbService;

    public GvAuthService(DatabaseService dbService) {
        this.dbService = dbService;
    }

    @Override
    public long authenticate(String token) {
        Connection db = dbService.getConnection();

        try {
            PreparedStatement st = db.prepareStatement("select core.get_civilization_by_token(?);");
            st.setString(1, token);
            ResultSet r = st.executeQuery();
            if (r.next()) {
                return r.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                db.close();
            } catch (SQLException e) {}
        }

        return 0;
    }

}