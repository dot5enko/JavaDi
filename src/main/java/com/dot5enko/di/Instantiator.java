package com.dot5enko.di;

import com.dot5enko.di.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import org.bson.Document;

public class Instantiator {

    private static Instantiator instance = null;
    private ServiceContainer sc = ServiceContainer.getInstance();

    public static Instantiator getInstance() {
        if (instance == null) {
            instance = new Instantiator();
        }
        return instance;
    }

    private Instantiator() {
    }

    public void injectStaticDependencies(Class<?> current) throws Exception {
        while (current != null) {
            for (Field it : current.getDeclaredFields()) {
                if (Modifier.isStatic(it.getModifiers())) {
                    Annotation anInject = it.getAnnotation(Inject.class);
                    if (anInject != null) {
                        it.setAccessible(true);
                        it.set(null, sc.get(sc.lookupServiceName(it.getType().getCanonicalName())));
                    } else {
                        InjectInstance anInjectInterface = it.getAnnotation(InjectInstance.class);
                        if (anInjectInterface != null) {
                            it.setAccessible(true);
                            it.set(null, sc.get(anInjectInterface.value()));
                        }
                    }
                }
            }
            current = current.getSuperclass();
        }
    }

    public Object injectInternalDependencies(Object newInstance) throws Exception {

        Class c = newInstance.getClass();
        // TODO: catch here sub exceptions

        Class current = c;
        while (current != null) {
            for (Field it : current.getDeclaredFields()) {
                Annotation[] fieldAnnotations = it.getAnnotations();
                Annotation anInject = it.getAnnotation(Inject.class);
                if (anInject != null) {
//                    System.out.println("trying to inject data into " + newInstance.getClass().getSimpleName() + "::" + it.getName());

                    it.setAccessible(true);
                    it.set(newInstance, sc.get(sc.lookupServiceName(it.getType().getCanonicalName())));
                } else {
                    InjectInstance anInjectInterface = it.getAnnotation(InjectInstance.class);
                    if (anInjectInterface != null) {
                        //System.out.println("trying to inject interface data into "+toInstantiate.getName()+"::"+it.getName());

                        it.setAccessible(true);
                        it.set(newInstance, sc.get(anInjectInterface.value()));
                    }
                }

            }
            current = current.getSuperclass();
        }

        return newInstance;

    }

    public Object instantiate(Class<?> toInstantiate) throws DependencyException {
        return this.instantiate(toInstantiate, null);
    }

    static class ExceptionHandler {

        public static String unfoldMessage(Throwable e) {

            StringBuilder result = new StringBuilder(e.getMessage());

            Throwable currentE = e.getCause();

            while (currentE != null) {
                result.append("\n Caused by ").append(currentE.getClass().getCanonicalName()).append(" ").append(currentE.getMessage());
                currentE = currentE.getCause();
            }

            return result.toString();
        }
    }

    public Object instantiate(Class<?> toInstantiate, Document options) throws DependencyException {
        try {
            Class c = Class.forName(toInstantiate.getName());

            try {
                Method setOptsMethod = c.getMethod("setOptions", Document.class);
                setOptsMethod.setAccessible(true);

                setOptsMethod.invoke(null, options);
            } catch (NoSuchMethodException e) {
//                System.out.println("setOptions is not find on service "+toInstantiate.getCanonicalName());
            }

            Constructor[] cc = c.getConstructors();

            ArrayList<Object> constructorParams = new ArrayList<Object>();
            Object newInstance = null;
            try {
                for (Parameter it : cc[0].getParameters()) {
                    if (it.isAnnotationPresent(InjectInstance.class)) {
                        InjectInstance anInject = it.getAnnotation(InjectInstance.class);
                        constructorParams.add(this.sc.get(anInject.value()));
                    } else {
                        constructorParams.add(this.sc.get(sc.lookupServiceName(it.getType().getCanonicalName())));
                    }
                }
                newInstance = cc[0].newInstance(constructorParams.toArray());
            } catch (Exception instExc) {
                throw new DependencyException("exception in constructor : " + ExceptionHandler.unfoldMessage(instExc));
            }

            // TODO: catch here sub exceptions
            return this.injectInternalDependencies(newInstance);

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

            // cache class methods to optimize
            Method[] methods = c.getMethods();
            Method method = null;

            // find method by name, not parameters
            // TODO check for another methods with such name, if such exists throw exception
            for (Method it : methods) {
                if (it.getName().equals(methodName)) {
                    method = it;
                    break;
                }
            }

            ArrayList methodParams = new ArrayList<Object>();

            int currentAddinationalParam = 0;

            for (Parameter parameter : method.getParameters()) {

                Class it = parameter.getType();
                InjectInstance anInjectInterface = parameter.getAnnotation(InjectInstance.class);

                if (anInjectInterface != null) {
                    //System.out.println("trying to inject interface implementation (" + anInjectInterface.value() + ") for " + it.getSimpleName() + " into method parameter " + toInvokeOn.getClass().getSimpleName() + "::" + methodName);

                    methodParams.add(sc.get(anInjectInterface.value()));
                } else {
                    try {
                        methodParams.add(this.sc.get(sc.lookupServiceName(it.getCanonicalName())));
                    } catch (DependencyException e) {
                        if (extraParams.size() > currentAddinationalParam && extraParams.get(currentAddinationalParam).getClass().getCanonicalName().equals(it.getCanonicalName())) {
                            methodParams.add(extraParams.get(currentAddinationalParam));
                            currentAddinationalParam++;
                        } else {
                            throw new DependencyException("no proper resource found in service container nor provided as external param for " + parameter.getType().getSimpleName());
                        }
                    }
                }
            }

            for (int i = currentAddinationalParam; i < extraParams.size(); i++) {
                methodParams.add(extraParams.get(i));
            }

            return method.invoke(toInvokeOn, methodParams.toArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DependencyException("Error while trying to invoke method " + toInvokeOn.getClass().getSimpleName() + "." + methodName + ": " + ex.getMessage());
        }
    }

}
