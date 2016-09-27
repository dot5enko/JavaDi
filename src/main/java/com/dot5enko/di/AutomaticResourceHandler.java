package com.dot5enko.di;

public class AutomaticResourceHandler implements DelayedResourceHandler {

    private Class<?> clazz;
    
    public AutomaticResourceHandler(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public Object initialize() throws DependencyException {
        return Instantiator.getInstance().instantiate(clazz);
    }

}
