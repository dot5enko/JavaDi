package com.dot5enko.di;

/**
 *
 * @author serhio
 */
public class Service {

    public Object object;
    public DelayedResourceHandler handler = null;
    public boolean shared = true;

    public synchronized Object getAllocator() throws DependencyException {
        if (!shared) {
            return this.handler.initialize();
        } else {
            if (this.handler != null) {
                this.object = this.handler.initialize();
            }

            return this.object;
        }
    }

}
