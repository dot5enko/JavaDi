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

    /**
     *
     * This method allows to invoke a class method without providing a
     * parameters types, which allows to dynamically change method injections
     * without changing any configuration
     */
    public Object invokeMethod(Object toInvokeOn, String methodName, ArrayList<Object> methodAddParams) throws DependencyException {
        try {

            Class c = toInvokeOn.getClass();

            Method[] methods = c.getMethods();

            Method method = null;
            ArrayList methodParams = new ArrayList<Object>();

            for (Method it : methods) {
                if (it.getName().equals(methodName)) {
                    return this.invokeMethod(toInvokeOn, methodName, methodAddParams, it.getParameterTypes());
                }
            }
            throw new DependencyException("No method " + methodName + " found to invoke on " + toInvokeOn.getClass().getName());
            
        } catch (Exception e) {
            throw new DependencyException("Error while invoking method " + methodName + ": " + e.getMessage());
        }
    }

    public Object invokeMethod(Object toInvokeOn, String methodName, Class... paramTypes) throws DependencyException {
        return this.invokeMethod(toInvokeOn, methodName, new ArrayList<Object>(), paramTypes);
    }

    /**
     * This method allows to invoke special method with similar signature
     * provided by name and parameter types
     */
    public Object invokeMethod(Object toInvokeOn, String methodName, ArrayList<Object> extraParams, Class... paramTypes) throws DependencyException {

        try {

            Class c = toInvokeOn.getClass();
            Method method = c.getMethod(methodName, paramTypes);

            ArrayList methodParams = new ArrayList<Object>();

            int currentAddinationalParam = 0;

            for (Class it : paramTypes) {
                try {
                    methodParams.add(this.sc.get(it.getCanonicalName()));
                } catch (DependencyException e) {
                    if (extraParams.get(currentAddinationalParam).getClass().getCanonicalName().equals(it.getCanonicalName())) {

                        methodParams.add(extraParams.get(currentAddinationalParam));
                        currentAddinationalParam++;
                    } else {
                        throw new DependencyException("Error invoking a method: no proper resource found in service container nor provided as external param");
                    }
                }
            }

            for (int i = currentAddinationalParam; i < extraParams.size(); i++) {
                methodParams.add(extraParams.get(i));
            }

            return method.invoke(toInvokeOn, methodParams.toArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DependencyException("Error while trying to invoke method  " + methodName + ": " + ex.getMessage());
        }
    }

}
