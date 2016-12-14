package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.annotations.HasManyToMany;
import com.dot5enko.database.exception.DaoObjectException;

@HasManyToMany(mediateFrom = "tag_id", mediate = PageToTag.class, mediateTo = "page_id", value = Page.class, alias = "Pages")
public class Tag extends DaoObject {

    public Tag(int primaryKey) throws DaoObjectException {
        super(primaryKey);
    }

    public Tag() {
    }

    public int id;

    public String value;
}
