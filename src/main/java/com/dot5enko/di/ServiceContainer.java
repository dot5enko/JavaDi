package com.dot5enko.di;

import java.util.ArrayList;
import java.util.HashMap;

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

    private static HashMap<String, Service> objects;
    private static HashMap<String, ArrayList<String>> canonicalMapping;

    private ServiceContainer() {
        this.objects = new HashMap<>();
        canonicalMapping = new HashMap<>();
    }
    
    private void addToMapping(String alias,Class<?> clazz) {
         ArrayList<String> aliases = canonicalMapping.get(clazz.getCanonicalName());

        if (aliases == null) {
            aliases = new ArrayList<String>();
            canonicalMapping.put(clazz.getCanonicalName(),aliases);
        }

        aliases.add(alias);
    }
    
    String lookupServiceName(String canonicalClassName){
        ArrayList<String> list =  canonicalMapping.get(canonicalClassName);
        if (list != null) {
            return list.get(0); // get the first element in list
        } 
        return null;
    }
    
    
    public void addLazyService(String name, DelayedResourceHandler h, Class<?> clazz) {
        Service newResource = new Service();
        newResource.handler = h;

        this.objects.put(name, newResource);
        addToMapping(name,clazz);
       
    }
    
    public void addNotSharedService(String name, DelayedResourceHandler h,Class<?> clazz) {
    
        Service newResource = new Service();
        newResource.handler = h;
        newResource.shared = false;
        
        this.objects.put(name, newResource);
        addToMapping(name,clazz);
    }
    

    public void addService(String name, Object resource) {
        Service newResource = new Service();
        newResource.object = resource;
        
        this.objects.put(name, newResource);
        addToMapping(name,resource.getClass());
    }

    public Object get(String canonicalName) throws DependencyException {
        if (this.objects.containsKey(canonicalName)) {
            return this.objects.get(canonicalName).getAllocator();
        } else {
            throw new DependencyException("No such object (" + canonicalName + ") in dependency injector container");
        }
    }

}
