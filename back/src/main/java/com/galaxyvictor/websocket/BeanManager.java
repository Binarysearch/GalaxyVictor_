package com.galaxyvictor.websocket;

import java.util.HashMap;

public class BeanManager {

    private static HashMap<Class<?>, Object> beans = new HashMap<>();

    @SuppressWarnings("unchecked")
	public static <T> T get(Class<? extends T> c) {
        Object bean = beans.get(c);
        if (bean == null) {
            throw new BeanNotFoundException("Bean of type '" + c.getName() + "' not found.");
        }
		return (T) bean;
	}
    
    public static <T> void addBean(Class<? extends T> c, T bean) {
        beans.put(c, bean);
    }
}