package com.dot5enko.di;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;

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
        this.addService("__self__", this);
    }

    private void addToMapping(String alias, Class<?> clazz) {
        ArrayList<String> aliases = canonicalMapping.get(clazz.getCanonicalName());

        if (aliases == null) {
            aliases = new ArrayList<String>();
            canonicalMapping.put(clazz.getCanonicalName(), aliases);
        }

        aliases.add(alias);
    }

    String lookupServiceName(String canonicalClassName) throws DependencyException {
        ArrayList<String> list = canonicalMapping.get(canonicalClassName);
        if (list != null) {
            return list.get(0); // get the first element in list
        }
        throw new DependencyException(canonicalClassName + " is not in service container now");
    }

    public void addLazyService(String name, DelayedResourceHandler h, Class<?> clazz) {
        Service newResource = new Service(name);
        newResource.handler = h;

        this.objects.put(name, newResource);
        addToMapping(name, clazz);

    }

    public void addLazyService(String name, Class<?> clazz) {
        Service s = new Service(name);
        s.handler = new AutomaticResourceHandler(clazz);
        objects.put(name, s);
        addToMapping(name, clazz);
    }

    public void addNotSharedService(String name, DelayedResourceHandler h, Class<?> clazz) {

        Service newResource = new Service(name);
        newResource.handler = h;
        newResource.shared = false;

        this.objects.put(name, newResource);
        addToMapping(name, clazz);
    }

    public void addNotSharedService(String name, Class<?> clazz) {

        Service newResource = new Service(name);
        newResource.handler = new AutomaticResourceHandler(clazz);
        newResource.shared = false;

        this.objects.put(name, newResource);
        addToMapping(name, clazz);
    }

    private void addRawResource(String name, Class<?> clazz, boolean lazy, boolean shared, Document options) throws DependencyException {
        Service newResource = new Service(name);
        newResource.options = options;

        if (lazy) {
            newResource.handler = new AutomaticResourceHandler(clazz);
        } else {
            try {
                newResource.object = Instantiator.getInstance().instantiate(clazz, options);
            } catch (DependencyException ex) {
                throw new DependencyException("Can't instantiate resource " + name + "(" + clazz.getName() + ")");
            }
        }

        newResource.shared = shared;

        this.objects.put(name, newResource);
        addToMapping(name, clazz);
    }

    public void addService(String name, Object resource) {

        Service newResource = new Service(name);
        newResource.object = resource;

        this.objects.put(name, newResource);
        addToMapping(name, resource.getClass());
    }

    public Object get(String canonicalName) throws DependencyException {
        if (this.objects.containsKey(canonicalName)) {
            return this.objects.get(canonicalName).getAllocator();
        } else {
            throw new DependencyException("No such object (" + canonicalName + ") in dependency injector container ");
        }
    }

    private boolean addIfresource(Class<?> clazz) throws DependencyException {
        boolean isService = clazz.isAnnotationPresent(com.dot5enko.di.annotation.Service.class);
        if (isService) {
            com.dot5enko.di.annotation.Service sAnot = clazz.getAnnotation(com.dot5enko.di.annotation.Service.class);
            String serviceName = sAnot.value();
            if (serviceName.equals("")) {
                serviceName = clazz.getCanonicalName();
            }

            boolean lazy = true;
            if (clazz.isAnnotationPresent(com.dot5enko.di.annotation.service.Lazy.class)) {
                lazy = clazz.getAnnotation(com.dot5enko.di.annotation.service.Lazy.class).value();
            }

            boolean shared = true;
            if (clazz.isAnnotationPresent(com.dot5enko.di.annotation.service.Shared.class)) {
                shared = clazz.getAnnotation(com.dot5enko.di.annotation.service.Shared.class).value();
            }

            try {
                this.addRawResource(serviceName, clazz, lazy, shared, new Document());
            } catch (Exception e) {
                throw new DependencyException("Can't add service for class " + clazz.getName() + " on autoloading");
            }

            return true;
        }

        return false;
    }

    public ServiceContainer initializeWithConfig(Document config) throws DependencyException {

        Set<Entry<String, Object>> entrySet = config.get("services", Document.class).entrySet();

        for (Entry<String, Object> cur : entrySet) {
            Document field = (Document) cur.getValue();
            String name = cur.getKey();

            // move next two initialization to function
            boolean shared = true;
            if (field.containsKey("shared")) {
                shared = field.getBoolean("shared");
            }

            boolean lazy = true;
            if (field.containsKey("lazy")) {
                lazy = field.getBoolean("lazy");
            }

            Document options = (Document) field.getOrDefault("options", new Document());

            try {
                this.addRawResource(name, Class.forName(field.getString("class")), lazy, shared, options);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServiceContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        List<String> autoloadPackages = config.get("autoload", List.class);
        for (String packageName : autoloadPackages) {
            for (Class<?> it : ClassFinder.find(packageName)) {
                this.addIfresource(it);
            }
        }

        List<String> autoload = config.get("initialize", List.class);
        for (String classToInitialize : autoload) {
            try {
                System.out.println("trying to inject static deps "+classToInitialize);
                Instantiator.getInstance().injectStaticDependencies(Class.forName(classToInitialize));
            } catch (Exception ex) {
                System.out.println("Error while instantiating resource: "+Instantiator.ExceptionHandler.unfoldMessage(ex));
            }
        }

        return this;

    }

}
