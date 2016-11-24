package com.dot5enko.test.mockup;

import com.dot5enko.di.annotation.InjectInstance;

public class StdOutLogger implements LogInterface {
    
    @InjectInstance("db")
    DatabaseInterface db;

    public void error(String x) {
        System.out.println("Error:"+x);
        db.executeQuery("Error:" + x);
    }

    public void log(String x) {
        System.out.println("Log:"+x);
        db.executeQuery("Log: " + x);
    }

}
