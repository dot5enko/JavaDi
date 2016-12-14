package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.annotations.HasOne;
import com.dot5enko.database.annotations.Table;
import com.dot5enko.database.exception.DaoObjectException;

@Table("page_to_tag")
@HasOne(from = "page_id", to = "id", value = Page.class)
@HasOne(from = "tag_id", to = "id", value = Tag.class)
public class PageToTag extends DaoObject {

    public PageToTag(int primaryKey) throws DaoObjectException {
        super(primaryKey);
    }

    public PageToTag() {
    }

    public int id;

    public int page_id;
    public int tag_id;
}
