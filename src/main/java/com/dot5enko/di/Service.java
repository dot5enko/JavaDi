package com.dot5enko.di;

/**
 *
 * @author serhio
 */
public class Service {

    public Object object;
    public DelayedResourceHandler handler = null;

    public Object getAllocator() throws DependencyException{
        if (this.handler != null) {
            this.object = this.handler.initialize();           
        }

        return this.object;
    }

}
