package com.dot5enko.server.protocols.http;

import com.dot5enko.di.ServiceContainer;
import com.dot5enko.di.annotation.Inject;

public class HttpController {

    protected HttpResponse response;
    protected HttpRequest request;

    @Inject
    protected ServiceContainer services;

    public HttpController(HttpRequest request, HttpResponse response) {
        this.response = response;
        this.request = request;
    }

}
