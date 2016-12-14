package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.annotations.HasMany;
import com.dot5enko.database.annotations.HasOne;
import com.dot5enko.database.annotations.Table;
import com.dot5enko.database.exception.DaoObjectException;
import java.util.HashMap;

@Table("resource_category")
@HasOne(from = "parent_id", value = ResourceCategory.class, alias = "Parent")
@HasMany(from = "type", value = Page.class, alias = "Pages")
public class ResourceCategory extends DaoObject {

    public ResourceCategory() {
    }

    public ResourceCategory(int primaryKey) throws DaoObjectException {
        super(primaryKey);
    }

    public ResourceCategory(HashMap<String, String> data) throws DaoObjectException {
        super(data);
    }

    public int id;

    public String name;

    public String seo_alias;

    public int parent_id;
}
