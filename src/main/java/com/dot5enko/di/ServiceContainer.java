package com.dot5enko.di;

import java.util.HashMap;

/**
 *
 * @author serhio
 */
public class ServiceContainer {
    
    private static ServiceContainer instance = null;
    
    public static  ServiceContainer getInstance() {
        if (instance == null) {
            instance = new ServiceContainer();
        }
        return instance;
    }
    
    private static HashMap<String,Resource> objects;

    private ServiceContainer() {
        this.objects = new HashMap<>();
    }
    
    public void addLazyResource(Class<?> name,DelayedResourceHandler h){
        Resource newResource = new Resource();
        newResource.handler = h;

        this.objects.put(name.getName(), newResource);
    }
    
    public void addResource(Class name,Object resource) {
       
    }
    
    public Object get(Class<?> name) throws DependencyException {
        if (this.objects.containsKey(name.getName())) {
            return this.objects.get(name.getName()).getAllocator();
        } else {
            throw new DependencyException("No such object in dependency injector container");
        }
    }
    
}
