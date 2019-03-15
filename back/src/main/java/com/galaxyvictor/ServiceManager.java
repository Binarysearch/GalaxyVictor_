package com.galaxyvictor;

import java.util.HashMap;

public class ServiceManager {

    private static HashMap<Class<?>, Object> services = new HashMap<>();

    @SuppressWarnings("unchecked")
	public static <T> T get(Class<? extends T> c) {
        Object service = services.get(c);
        if (service == null) {
            throw new ServiceNotFoundException("Service of type '" + c.getName() + "' not found.");
        }
		return (T) service;
	}
    
    public static <T> void addService(Class<? extends T> c, T bean) {
        services.put(c, bean);
    }
}