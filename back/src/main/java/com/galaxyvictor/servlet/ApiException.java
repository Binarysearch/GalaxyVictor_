package com.galaxyvictor.servlet;


public class ApiException extends HttpException {

    private static final long serialVersionUID = -1594823546455620036L;

    private DbResponseError errorData;

    public ApiException(DbResponseError errorData) {
        super(errorData.getCode());
        this.errorData = errorData;
    }

    /**
     * @return the errorData
     */
    public DbResponseError getErrorData() {
        return errorData;
    }

    /**
     * @param errorData the errorData to set
     */
    public void setErrorData(DbResponseError errorData) {
        this.errorData = errorData;
    }

    
}