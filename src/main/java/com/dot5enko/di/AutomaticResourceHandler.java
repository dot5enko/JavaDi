package com.dot5enko.di;

import org.bson.Document;

public class AutomaticResourceHandler implements DelayedResourceHandler {

    private Class<?> clazz;
    
    public AutomaticResourceHandler(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public Object initialize(Document options) throws DependencyException {
        return Instantiator.getInstance().instantiate(clazz,options);
    }

}
