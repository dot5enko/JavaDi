package com.dot5enko.test.controllers;

import com.dot5enko.database.Dao;
import com.dot5enko.database.DaoObject;
import com.dot5enko.database.Objects.Page;
import com.dot5enko.database.Objects.SomeShit;
import com.dot5enko.database.exception.DaoObjectException;
import com.dot5enko.database.Objects.Tag;
import com.dot5enko.database.exception.ExecutingQueryException;
import com.dot5enko.server.protocols.http.HttpController;
import com.dot5enko.server.protocols.http.HttpRequest;
import com.dot5enko.server.protocols.http.HttpResponse;
import com.dot5enko.test.TimeService;
import java.util.Vector;

public class IndexController extends HttpController {

    public IndexController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }
    
    
    public void shitAction(Dao db) throws DaoObjectException, ExecutingQueryException {
        
        
        
        Vector<SomeShit> allShit = db.executeRawQuery("SELECT * from shit").parseObjects(new SomeShit());
        
        
        
        
        System.out.println(allShit);
        
    }
    
    
    public void pageAction() {
        try {
            Page p = new Page(Integer.parseInt(parameters.getOrDefault("id", "0")));

            StringBuilder tags = new StringBuilder();

            for (DaoObject it : p.get("Tags")) {
                tags.append("<p>" + ((Tag) it).value + "</p>");
            }
            
            response.put(p.body + "<h2>tags</h2>" + tags.toString());

        } catch (DaoObjectException e) {
            response.setContent("Error while getting page:" + e.getMessage());
        }

    }

    public void getAllTagsAction(Dao db) {
        try {

            String tagName = parameters.getOrDefault("tag", "");
            try {

                Vector<Tag> tags = db.find(new Tag());

                StringBuilder out = new StringBuilder();

                for (Tag it : tags) {
                    response.put("<p>" + it.value + "</p>");
                }
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
        long start = time.currentTime();
        
        try {
            String tagName = parameters.getOrDefault("tag", "");
            try {

                Tag tag = db.Where((x) -> {
                    return ((Tag) x).value.equals(tagName);
                }).find(new Tag()).firstElement();

                Vector<Page> pages = tag.get("Pages");

                if (pages == null) {
                    response.setContent("No pages with tag <strong>" + tag.value + "</strong>");
                } else {
                    response.put("<h2>List of pages with tag <strong>" + tag.value + "</strong></h2>");
                    for (Page it : pages) {
                        response.put("<p> => " + it.titleValue + "</p>");
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
        
        response.put("<h2 style=color:red>"+elapsed+"</h2>");
    }

}
