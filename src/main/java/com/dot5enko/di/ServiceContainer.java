package com.dot5enko.di;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author serhio
 */
public class ServiceContainer {

    private static ServiceContainer instance = null;

    public static ServiceContainer getInstance() {
        if (instance == null) {
            instance = new ServiceContainer();
        }
        return instance;
    }

    private static HashMap<String, Resource> objects;

    private ServiceContainer() {
        this.objects = new HashMap<>();
    }

    public void addLazyResource(Class<?> name, DelayedResourceHandler h) {
        Resource newResource = new Resource();
        newResource.handler = h;
        
        this.objects.put(name.getCanonicalName(), newResource);
    }

    public void addResource(Class name, Object resource) {
        Resource newResource = new Resource();
        newResource.object = resource;

        this.objects.put(name.getCanonicalName(), newResource);
    }

    public Object get(String canonicalName) throws DependencyException {
       if (this.objects.containsKey(canonicalName)) {
            return this.objects.get(canonicalName).getAllocator();
        } else {
            throw new DependencyException("No such object in dependency injector container");
        }
    }

    public Object get(Class<?> name) throws DependencyException {
        return this.get(name.getCanonicalName());
    }

}
