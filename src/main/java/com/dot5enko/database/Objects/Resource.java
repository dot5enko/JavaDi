package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;

public class Resource extends DaoObject {

    public int id;

    public String text;

    public String path;

    public int category_id;

    @Override
    public void setup() {
        this.hasOne("category_id","id",ResourceCategory.class);
    }
}
