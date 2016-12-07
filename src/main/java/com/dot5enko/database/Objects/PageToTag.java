package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.annotations.Table;
import com.dot5enko.database.exception.DaoObjectException;

@Table("page_to_tag")
public class PageToTag extends DaoObject {

    public PageToTag(int primaryKey) throws DaoObjectException {
        super(primaryKey);
    }

    public PageToTag() {
    }

    public int id;

    public int page_id;
    public int tag_id;

    public void setup() {
        this.hasOne("page_id", "id", Page.class);
        this.hasOne("tag_id", "id", Tag.class);
    }
}
