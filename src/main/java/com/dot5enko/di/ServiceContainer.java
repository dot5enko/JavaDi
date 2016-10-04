package com.dot5enko.di;

import com.fasterxml.jackson.core.JsonFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    
    String lookupServiceName(String canonicalClassName) throws DependencyException{
        ArrayList<String> list =  canonicalMapping.get(canonicalClassName);
        if (list != null) {
            return list.get(0); // get the first element in list
        } 
        throw new DependencyException(canonicalClassName+" is not in service container now");
    }
    
    
    public void addLazyService(String name, DelayedResourceHandler h, Class<?> clazz) {
        Service newResource = new Service(name);
        newResource.handler = h;

        this.objects.put(name, newResource);
        addToMapping(name,clazz);
       
    }
    
    public void addLazyService(String name,Class<?> clazz) {
        Service s = new Service(name);
        s.handler = new AutomaticResourceHandler(clazz);
        objects.put(name, s);
        addToMapping(name,clazz);
    }
    
    public void addNotSharedService(String name, DelayedResourceHandler h,Class<?> clazz) {
    
        Service newResource = new Service(name);
        newResource.handler = h;
        newResource.shared = false;
        
        this.objects.put(name, newResource);
        addToMapping(name,clazz);
    }
    public void addNotSharedService(String name, Class<?> clazz) {
    
        Service newResource = new Service(name);
        newResource.handler = new AutomaticResourceHandler(clazz);
        newResource.shared = false;
        
        this.objects.put(name, newResource);
        addToMapping(name,clazz);
    }
    
    private void addRawResource(String name,Class<?> clazz,boolean lazy,boolean shared) throws DependencyException {
        Service newResource = new Service(name);
        if (lazy) {
            newResource.handler = new AutomaticResourceHandler(clazz);
        } else {
            try {
                newResource.object = Instantiator.getInstance().instantiate(clazz);
            } catch (DependencyException ex) {
                throw new DependencyException("Can't instantiate resource "+name+"("+clazz.getName()+")");
            }
        }
        
        newResource.shared = shared;
        
        this.objects.put(name, newResource);
        addToMapping(name,clazz);
    }

    public void addService(String name, Object resource) {
        Service newResource = new Service(name);
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
    
    public void initializeWithConfig(String path) throws DependencyException {

        try {
            JsonFactory factory = new JsonFactory();

            ObjectMapper mapper = new ObjectMapper(factory);
            JsonNode rootNode = mapper.readTree(new File(path));

            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
            while (fieldsIterator.hasNext()) {

                Map.Entry<String, JsonNode> cur = fieldsIterator.next();
                
                
                JsonNode field = cur.getValue();
                String name = cur.getKey();
                
                
                // move next two initialization to function
                boolean shared = true;
                if (field.has("shared")) {
                    shared = field.get("shared").asBoolean();
                }
                
                boolean lazy = true;
                if (field.has("lazy")) {
                    lazy = field.get("lazy").asBoolean();
                }
                
                this.addRawResource(name, Class.forName(field.get("class").asText()), lazy, shared);
            }
        } catch (Exception ex) {
            throw new DependencyException("Error initializing service container from configuration file: "+ex.getMessage());
        }

    }
    

}
