package com.dot5enko.test.mockup;

public class Database {
    
    private int insertId = 0;
    
    public void RunQuery(String query) {
        // this method does nothing
        this.insertId++;
    }
    
    public int getLastInsertId(){
        return this.insertId;
    }
}
