package com.dot5enko.test.mockup;

public class IndexController {
    
    FormatHelper formatter;
    
    public IndexController(FormatHelper fh){
        this.formatter = fh;
    }
    
    public String indexAction(Request request) {
        return "Hello ,"+request.getUserAgent()+this.formatter.toUpper("This is the uppercased greeting from ")+"IndexController.indexAction";
    }
    
    
    public String cabinetAction(Request request, Database db, String username) {
        return "Hi, "+username+", your ip is "+request.getRemoteAdress()+" lastInsertId :"+db.getLastInsertId();
    }
    
}
