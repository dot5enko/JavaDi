package com.dot5enko.test.controllers;

import com.dot5enko.di.annotation.InjectInstance;
import com.dot5enko.server.protocols.http.HttpController;
import com.dot5enko.server.protocols.http.HttpRequest;
import com.dot5enko.server.protocols.http.HttpResponse;
import com.dot5enko.test.mockup.LogInterface;
import java.util.Map;

public class HelloController extends HttpController {

    public HelloController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    public void worldAction() {

//        log.error("s");
        response.setContent("This is the world action ");
    }

    public void logAction(@InjectInstance("logger") LogInterface log) {

        Map<String, String> parameters = request.getParameters();

        log.error(parameters.getOrDefault("data", "null"));
        
        response.setContent(parameters.toString());

    }

}
