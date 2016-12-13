package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.annotations.Column;
import com.dot5enko.database.annotations.Table;
import com.dot5enko.database.exception.DaoObjectException;
import java.util.HashMap;

@Table(value="shit",lazy=false)
public class SomeShit extends DaoObject {

    public SomeShit() {
    }

    public SomeShit(int primaryKey) throws DaoObjectException {
        super(primaryKey);
    }

    public SomeShit(HashMap<String, String> data) throws DaoObjectException {
        super(data);
    }
    
    public int id;

    @Column("someVal")
    public String value;
    
}
