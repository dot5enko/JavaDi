package com.dot5enko.di;

/**
 *
 * @author serhio
 */
public interface DelayedResourceHandler<T> {
    public T initialize() throws DependencyException;
}
