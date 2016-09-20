package com.dot5enko.di;

public class Instantiator {
    
    private ServiceContainer sc = ServiceContainer.getInstance();
    
    public Object instantiate(Class<?> toInstantiate) throws DependencyException {
        
//        ReflectionClass rClass = new ReflectionClass();
        throw new DependencyException("Not yet implemented");
        
    }
    
    
}
