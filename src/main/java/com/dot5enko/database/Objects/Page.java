package com.dot5enko.database.Objects;

import com.dot5enko.database.annotations.Column;
import com.dot5enko.database.DaoObject;
import com.dot5enko.database.Objects.ResourceCategory;
import com.dot5enko.database.exception.DaoObjectException;

public class Page extends DaoObject {

    public Page(int primaryKey) throws DaoObjectException {
        super(primaryKey);
    }

    public Page() {
    }

    public int id;

    public int type;

    @Column("title")
    public String titleValue;

    public String body;

    public void setup() {
        this.hasOne(ResourceCategory.class, "id", "type");
    }
}
