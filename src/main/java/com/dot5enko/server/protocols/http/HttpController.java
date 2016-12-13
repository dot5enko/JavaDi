package com.dot5enko.server.protocols.http;

import com.dot5enko.di.ServiceContainer;
import com.dot5enko.di.annotation.Inject;
import java.util.Map;

public class HttpController {

    protected HttpResponse response;
    protected HttpRequest request;
    
    protected Map<String, String> parameters;

    @Inject
    protected ServiceContainer services;

    public HttpController(HttpRequest request, HttpResponse response) {
        this.response = response;
        response.setContent("");
        
        this.request = request;
        
        this.parameters = request.getParameters();
        
    }

}
