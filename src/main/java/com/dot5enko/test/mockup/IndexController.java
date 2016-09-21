package com.dot5enko.test.mockup;

import com.dot5enko.di.annotation.Inject;
import com.dot5enko.di.annotation.InjectInstance;

public class IndexController {
    
    @Inject
    private Logger logger;
    
    @Inject
    FormatHelper formatter;
    
    public String indexAction(Request request) {
        return "Hello ," + request.getUserAgent() + this.formatter.toUpper("This is the uppercased greeting from ") + "IndexController.indexAction";
    }
    
    public String cabinetAction(Request request, MysqlDatabase db, String username) {
        
        this.logger.log("cabinet action executed");
        
        db.executeQuery("INSERT INTO some_log_table SET time = " + request.getRequestTime().getTime() + " AND ua = " + request.getUserAgent());
        
        return "Hi, " + username + ", your ip is " + request.getRemoteAdress() + " lastInsertId :" + db.getLastInsertId();
    }
    
}
