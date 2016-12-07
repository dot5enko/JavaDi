package com.dot5enko.test.controllers;

import com.dot5enko.database.Dao;
import com.dot5enko.database.DaoObject;
import com.dot5enko.database.Objects.Page;
import com.dot5enko.database.exception.DaoObjectException;
import com.dot5enko.database.Objects.ResourceCategory;
import com.dot5enko.database.Objects.Tag;
import com.dot5enko.server.protocols.http.HttpController;
import com.dot5enko.server.protocols.http.HttpRequest;
import com.dot5enko.server.protocols.http.HttpResponse;
import com.dot5enko.test.TimeService;
import java.util.Map;
import java.util.Vector;

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

            StringBuilder tags = new StringBuilder();

            for (DaoObject it : p.get("Tags")) {
                tags.append("<p>" + ((Tag) it).value + "</p>");
            }

            response.setContent(p.body + "<h2>tags</h2>" + tags.toString());

            p.remove();

        } catch (DaoObjectException e) {
            response.setContent("Error while getting page:" + e.getMessage());
        }

    }

    public void getAllTagsAction(Dao db) {

        Map<String, String> parameters = request.getParameters();

        try {

            String tagName = parameters.getOrDefault("tag", "");
            try {

                Vector<Tag> tags = db.find(new Tag());

                StringBuilder out = new StringBuilder();

                for (Tag it : tags) {
                    out.append("<p>" + it.value + "</p>");
                }

                response.setContent(out.toString());

            } catch (Exception e) {
                e.printStackTrace();
                this.response.setContent("<h1>tag <b>" + tagName + "</b> not exists</h1>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setContent("Error: " + e.getMessage());
        }

    }

    public void getArticlesByTagAction(Dao db,TimeService time) {

        Map<String, String> parameters = request.getParameters();
        StringBuilder out = new StringBuilder();
        
        long start = time.currentTime();
        
        try {
            String tagName = parameters.getOrDefault("tag", "");
            try {

                Tag tag = db.Where((x) -> {
                    return ((Tag) x).value.equals(tagName);
                }).find(new Tag()).firstElement();

                Vector<Page> pages = tag.get("Pages");

                if (pages == null) {
                    out.setLength(0);
                    out.append("No pages with tag <strong>" + tag.value + "</strong>");
                } else {
                    out.append("<h2>List of pages with tag <strong>" + tag.value + "</strong></h2>");
                    for (Page it : pages) {
                        out.append("<p> => " + it.titleValue + "</p>");
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
                response.setContent("<h1>tag <b>" + tagName + "</b> not exists</h1>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setContent("Error: " + e.getMessage());
        }
        
        long elapsed = time.currentTime() - start;
        
        out.append("<h2 style=color:red>"+elapsed+"</h2>");
        
        response.setContent(out.toString());
    }

}
