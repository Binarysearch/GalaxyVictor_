package com.galaxyvictor.util;

import java.sql.SQLException;

import com.galaxyvictor.ServiceManager;
import com.galaxyvictor.db.DatabaseService;
import com.galaxyvictor.servlet.DbResponse;

public class DbCreatedAsincTask extends FutureEvent {

    private long id;
    private double endTime;
    private String procedureName;

    @Override
    public double getEndTime() {
        return endTime;
    }

    @Override
    public void finish() {
        try {
            DbResponse dbResponse = ServiceManager.get(DatabaseService.class).executeQueryForObject("select "+procedureName+"(?);", DbResponse.class, id);
            ServiceManager.get(DbOrderExecutorService.class).executeDbOrder(dbResponse);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the procedureName
     */
    public String getProcedureName() {
        return procedureName;
    }

    /**
     * @param procedureName the procedureName to set
     */
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

}