package com.dot5enko.di;

/**
 *
 * @author serhio
 */
public class Resource<T> {

    public T object;
    public DelayedResourceHandler<T> handler = null;

    public T getAllocator() throws DependencyException{
        if (this.handler != null) {
            this.object = (T) this.handler.initialize();
            
        }

        return this.object;
    }

}
