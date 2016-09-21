package com.dot5enko.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class Instantiator {

    private ServiceContainer sc = ServiceContainer.getInstance();

    public Object instantiate(Class<?> toInstantiate) throws DependencyException {

        try {

            Class c = Class.forName(toInstantiate.getName());
            Constructor[] cc = c.getConstructors();

            ArrayList<Object> constructorParams = new ArrayList<Object>();

            // Fixme: check for constructor existance
            try {
                for (Parameter it : cc[0].getParameters()) {
                    constructorParams.add(this.sc.get(it.getType().getCanonicalName()));
                }
            } catch (DependencyException e) {
                throw new DependencyException(e.getMessage() + " When trying to instantiate " + toInstantiate.getName());
            }

            return cc[0].newInstance(constructorParams.toArray());

        } catch (Exception ex) {
            throw new DependencyException("Error while trying to instantiate " + toInstantiate.getName() + ": " + ex.getMessage());
        }
    }

    public Object invokeMethod(Object toInvokeOn, String methodName, Class... paramTypes) throws DependencyException {
        return this.invokeMethod(toInvokeOn, methodName, new ArrayList<Object>(), paramTypes);
    }

    public Object invokeMethod(Object toInvokeOn, String methodName, ArrayList<Object> extraParams, Class... paramTypes) throws DependencyException {

        try {

            Class c = toInvokeOn.getClass();
            Method method = c.getMethod(methodName, paramTypes);

            ArrayList methodParams = new ArrayList<Object>();

            for (Class it : paramTypes) {
                try {
                    methodParams.add(this.sc.get(it.getCanonicalName()));
                } catch (DependencyException e) {
                    // skip not found errors because there is extraParams
                }
            }

            methodParams.addAll(extraParams);

            return method.invoke(toInvokeOn, methodParams.toArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DependencyException("Error while trying to invoke method  " + methodName + ": " + ex.getMessage());
        }
    }

}
