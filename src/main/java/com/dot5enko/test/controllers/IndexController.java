package com.dot5enko.test.controllers;

import com.dot5enko.database.exception.DaoObjectException;
import com.dot5enko.database.Objects.Page;
import com.dot5enko.database.Objects.ResourceCategory;
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

            System.out.println(p.getOne("ResourceCategory").getOne("Parent").getOne("Parent"));

            response.setContent(p.body);
        } catch (DaoObjectException e) {
            response.setContent("Error while getting page:" + e.getMessage());
        }

    }

}
