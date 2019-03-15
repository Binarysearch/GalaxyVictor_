package com.galaxyvictor;


public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(String message) {
        super(message);
	}

	private static final long serialVersionUID = -6179819228630652748L;

}