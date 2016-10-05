package com.dot5enko.test.mockup;

import com.dot5enko.di.annotation.Inject;
import com.dot5enko.di.annotation.InjectInstance;

public class IndexController {
    
    @Inject
    private Logger logger;
    
    public IndexController(@InjectInstance("req") RequestInterface x){
        x.getRequestTime();
    }
    
    public String cabinetAction(@InjectInstance("req") RequestInterface request, @InjectInstance("db") DatabaseInterface db, String username) {
        
        this.logger.log("cabinet action executed");
        
        db.executeQuery("INSERT INTO some_log_table SET time = " + request.getRequestTime().getTime() + " AND u");
        
        return "Hi,"; //" + username + ", your ip is " + request.getRemoteAdress() + " requestTIme  :"+request.getRequestTime();
    }
    
}
