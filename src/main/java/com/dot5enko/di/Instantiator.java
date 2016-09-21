package com.dot5enko.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class Instantiator {

    private ServiceContainer sc = ServiceContainer.getInstance();

    public Object instantiate(Class<?> toInstantiate) throws DependencyException {

        try {

            Class c = Class.forName(toInstantiate.getName());
            Constructor[] cc = c.getConstructors();

//            System.out.println(java.util.Arrays.toString());
            ArrayList<Object> constructorParams = new ArrayList<Object>();
            try {
                for (Parameter it : cc[0].getParameters()) {
                    constructorParams.add(this.sc.get(it.getType().getCanonicalName()));
                }
            } catch (DependencyException e) {
                throw new DependencyException(e.getMessage() + " When trying to instantiate " + toInstantiate.getName());
            }

            return cc[0].newInstance(constructorParams.toArray());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Object invokeMethod(Object toInvokeOn, String methodName) throws DependencyException {
        throw new DependencyException("Not yet implemented");
    }

}
