package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.exception.DaoObjectException;

public class Tag extends DaoObject {

    public Tag(int primaryKey) throws DaoObjectException {
        super(primaryKey);
    }

    public Tag() {
    }

    public int id;

    public String value;

    @Override
    public void setup() {
        this.hasManyToMany("id", "tag_id", PageToTag.class, "page_id", "id", Page.class, "Pages");
    }
}
