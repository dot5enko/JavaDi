package com.dot5enko.database.Objects;

import com.dot5enko.database.annotations.Column;
import com.dot5enko.database.DaoObject;
import com.dot5enko.database.annotations.*;
import com.dot5enko.database.exception.DaoObjectException;

@Table(lazy = false)
@HasOne(from = "type", to = "id", value = ResourceCategory.class)
@HasManyToMany(from = "id", mediateFrom = "page_id", mediate = PageToTag.class, mediateTo = "tag_id", to = "id", value = Tag.class, alias = "Tags")
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
}
