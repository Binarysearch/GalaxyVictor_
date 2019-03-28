package com.galaxyvictor.servlet;


public class GvApiRequest {
    
    private final String procedureName;
    private final Object[] dbParams;

    public GvApiRequest(String procedureName, Object... dbParams) {
        this.procedureName = procedureName;
        this.dbParams = dbParams;
    }

    /**
     * @return the procedureName
     */
    public String getProcedureName() {
        return procedureName;
    }

    /**
     * @return the dbParams
     */
    public Object[] getDbParams() {
        return dbParams;
    }

}