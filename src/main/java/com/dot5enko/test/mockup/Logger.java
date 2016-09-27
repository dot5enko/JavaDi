package com.dot5enko.test.mockup;

import com.dot5enko.di.annotation.InjectInstance;

public class Logger {
    
    @InjectInstance("db")
    DatabaseInterface db; 
    
    public void error(String x) {
        db.executeQuery("Insert into error ..."+x);
    }
    public void log(String x) {
        db.executeQuery("Insert into log ... "+x);
    }
    
}
