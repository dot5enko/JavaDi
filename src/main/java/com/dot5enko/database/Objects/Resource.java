package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.annotations.HasOne;

@HasOne(from = "category_id", value = ResourceCategory.class)
public class Resource extends DaoObject {

    public int id;

    public String text;

    public String path;

    public int category_id;
}
