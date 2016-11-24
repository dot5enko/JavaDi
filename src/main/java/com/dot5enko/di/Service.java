package com.dot5enko.di;

import org.bson.Document;

/**
 *
 * @author serhio
 */
public class Service {

    public Object object;
    public DelayedResourceHandler handler = null;
    public boolean shared = true;
    public String name = "";
    public Document options;

    public Service(String name) {
        this.name = name;
    }

    public synchronized Object getAllocator() throws DependencyException {     
        if (!shared) {
            return this.handler.initialize(this.options);
        } else {
            if (this.handler != null && this.object == null) {
                this.object = this.handler.initialize(this.options);
            }
            return this.object;
        }
    }

}
