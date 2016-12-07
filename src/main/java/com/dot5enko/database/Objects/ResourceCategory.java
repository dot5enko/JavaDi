package com.dot5enko.database.Objects;

import com.dot5enko.database.DaoObject;
import com.dot5enko.database.annotations.Table;
import com.dot5enko.database.exception.DaoObjectException;
import java.util.HashMap;

@Table("resource_category")
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

    public void setup() {
        this.hasOne("id", "parent_id", ResourceCategory.class, "Parent");
        this.hasMany("type", "id", Page.class, "Pages");
    }
}
