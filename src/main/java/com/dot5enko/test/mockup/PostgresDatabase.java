package com.dot5enko.test.mockup;

public class PostgresDatabase implements DatabaseInterface {

    public void executeQuery(String q) {
        System.out.println("POSTGRES:"+q);
    }

}
