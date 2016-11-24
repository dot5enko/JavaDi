package com.dot5enko.di;

import org.bson.Document;

/**
 *
 * @author serhio
 */
public interface DelayedResourceHandler {
    public Object initialize(Document options) throws DependencyException;
}
