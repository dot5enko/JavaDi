package com.dot5enko.test.controllers;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.exception.DaoObjectException;
import com.dot5enko.database.Objects.Page;
import com.dot5enko.database.Objects.ResourceCategory;
import com.dot5enko.database.Objects.Tag;
import com.dot5enko.server.protocols.http.HttpController;
import com.dot5enko.server.protocols.http.HttpRequest;
import com.dot5enko.server.protocols.http.HttpResponse;
import java.util.Map;

public class IndexController extends HttpController {

    public IndexController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    public void categoryAction() {
        Map<String, String> parameters = request.getParameters();

        try {
            ResourceCategory p = new ResourceCategory(Integer.parseInt(parameters.getOrDefault("id", "0")));

            System.out.println(p.get("Pages"));

            response.setContent(p.name);
        } catch (DaoObjectException e) {
            response.setContent("Error while getting page:" + e.getMessage());
        }
    }
    
    public void pageAction() {

        Map<String, String> parameters = request.getParameters();

        try {
            Page p = new Page(Integer.parseInt(parameters.getOrDefault("id", "0")));

            StringBuilder tags =  new StringBuilder();
            
            for (DaoObject it: p.get("Tags")) {
                tags.append("<p>"+((Tag)it).value+"</p>");
            }
            
            
            
            response.setContent(p.body+"<h2>tags</h2>"+tags.toString());
            
            p.remove();
            
        } catch (DaoObjectException e) {
            response.setContent("Error while getting page:" + e.getMessage());
        }

    }

}
