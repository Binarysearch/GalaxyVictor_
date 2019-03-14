package com.galaxyvictor.servlet;


public class MethodNotAllowedException extends HttpException {

    private static final long serialVersionUID = 1L;

    public MethodNotAllowedException(){
        super(405);
    }
}