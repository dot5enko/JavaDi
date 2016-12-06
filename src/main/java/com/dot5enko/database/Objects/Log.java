package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.exception.DaoObjectException;


public class Log extends DaoObject {

    public String value;

    public int date;

    public Log() {
    }

    public int id;

    public Log(int primaryKey) throws DaoObjectException {
        super(primaryKey);
    }
}
