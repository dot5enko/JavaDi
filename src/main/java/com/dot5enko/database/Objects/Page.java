package com.dot5enko.database.Objects;

import com.dot5enko.database.annotations.Column;
import com.dot5enko.database.DaoObject;
import com.dot5enko.database.exception.DaoObjectException;
import com.dot5enko.database.annotations.Table;

public class Page extends DaoObject {

    public Page(int primaryKey) throws DaoObjectException {
        super(primaryKey);
    }

    public Page() {
    }

    public int id;

    @Column("title")
    public String titleValue;

    public String body;

}
