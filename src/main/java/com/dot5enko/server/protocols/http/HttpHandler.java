package com.dot5enko.server.protocols.http;

import com.dot5enko.di.Instantiator;
import com.dot5enko.server.ConnectionHandler;
import com.dot5enko.server.Request;
import com.dot5enko.server.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;

public class HttpHandler extends ConnectionHandler {

    private String controllersPackage;

    public HttpHandler(String pkg) {
        controllersPackage = pkg;
    }

    protected Response action(com.dot5enko.server.Request req) {

        String ControllerName = "";
        String MethodName = "";

        try {
            HttpRequest request = (HttpRequest) req;
            HttpResponse response = new HttpResponse();
            
            System.out.println("--> URI:"+request.getUri());
            
            String[] parts = request.getUri().split("\\?")[0].split("/");
            
            if (parts.length < 3) {
                throw new Exception("Wrong command");
            }
            
            ControllerName = parts[1].substring(0,1).toUpperCase()+parts[1].substring(1) + "Controller";

            Class<?> clazz = Class.forName(controllersPackage + '.' + ControllerName);
            Constructor<?> controller = clazz.getDeclaredConstructor(HttpRequest.class, HttpResponse.class);
            
            Instantiator instantiator = Instantiator.getInstance();
            
            HttpController controllerInstance = (HttpController) controller.newInstance(request,response);
            instantiator.injectInternalDependencies(controllerInstance);
            
            MethodName = parts[2] + "Action";
            
            instantiator.invokeMethod(controllerInstance,MethodName);
            return response;

        } catch (NoSuchMethodException ex) {
            System.out.println("No such action exists `" + MethodName + "` on controller `" + ControllerName + "`:"+ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("No such controller exists `" + ControllerName + "`");
        } catch (java.lang.Exception ex) {
            System.out.println("Unknown error:" + ex.getMessage());
        }

        HttpResponse empty = new HttpResponse();
        return empty.setContent("Error 500");

    }

    protected Request getRequest() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        try {
            while (true) {
                String sLine = br.readLine();
                if (sLine == null || sLine.trim().length() == 0) {
                    break;
                }
                sb.append(sLine + "\n");
            }
            return new HttpRequest(sb.toString());
        } catch (java.lang.Exception e) {
            throw new Exception("Can't parse request: " + e.getMessage());
        }
    }
}
