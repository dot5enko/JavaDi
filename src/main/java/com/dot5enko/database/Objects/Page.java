package com.dot5enko.database.Objects;

import com.dot5enko.database.annotations.Column;
import com.dot5enko.database.DaoObject;
import com.dot5enko.database.annotations.Table;
import com.dot5enko.database.exception.DaoObjectException;

@Table(lazy = false)
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
        this.hasOne("type", "id", ResourceCategory.class);
        this.hasManyToMany("id", "page_id", PageToTag.class, "tag_id", "id", Tag.class, "Tags");
    }
}
