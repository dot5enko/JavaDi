package com.dot5enko.test.mockup;

import com.dot5enko.di.annotation.Inject;
import com.dot5enko.di.annotation.InjectInstance;

public class IndexController {
    
    @Inject
    private Logger logger;
    
    public IndexController(Request x){
        x.getRequestTime();
    }
    
    public String cabinetAction(Request request, @InjectInstance("db") DatabaseInterface db, String username) {
        
        this.logger.log("cabinet action executed");
        
        db.executeQuery("INSERT INTO some_log_table SET time = " + request.getRequestTime().getTime() + " AND ua = " + request.getUserAgent());
        
        return "Hi, " + username + ", your ip is " + request.getRemoteAdress() + " requestTIme  :"+request.getRequestTime();
    }
    
}
