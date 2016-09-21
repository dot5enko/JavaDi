package com.dot5enko.test.mockup;

public class MysqlDatabase implements DatabaseInterface {
    
    private int insertId = 0;
    
    public void executeQuery(String query) {
        // this method does nothing
        this.insertId++;
    }
    
    public int getLastInsertId(){
        return this.insertId;
    }
}
