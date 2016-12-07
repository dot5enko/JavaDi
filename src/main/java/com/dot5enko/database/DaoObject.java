package com.dot5enko.database;

import com.dot5enko.database.exception.DaoObjectException;
import com.dot5enko.database.annotations.Table;
import com.dot5enko.database.annotations.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import com.dot5enko.database.Dao.CacheItem;
import com.dot5enko.database.exception.ExecutingQueryException;
import com.dot5enko.di.annotation.Inject;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

//import sun.tools.tree.ThisExpression;
abstract public class DaoObject {

    class FieldInfo {

        public String name;
        public String type;
        public boolean required; // is field required to save
        public boolean timestamp; // field would be overwritten by current timestamp on save()

    }

    static class RelationOptions {

        final static int ONETOONE = 1;
        final static int ONETOMANY = 2;
        final static int MANYTOMANY = 3;

        HashMap<String, String> opts = new HashMap();

        Class<?> clazz;
        Class<?> middle;

        int type;
    }

    private String _tableName = "";
    private String _primaryKey = "";
    private boolean _isLoaded = false;

    private HashMap<String, DaoResult> relations = new HashMap();

    // this is very bad. need to be static for Dao Class
    private HashMap<String, RelationOptions> relOpts = new HashMap();

    @Inject
    private static Dao db;

    private final static String DEFAULT_PRIMARY = "id";
    private HashMap<String, FieldInfo> FIELDS = new HashMap<String, FieldInfo>();

    public DaoObject() {
        this.initialize();
    }

    // relation methods
    protected DaoObject hasOne(String keyFrom, String keyTo, Class<?> clazz, String relationName) {

        RelationOptions otps = new RelationOptions();
        otps.opts.put("keyTo", keyTo);
        otps.opts.put("keyFrom", keyFrom);

        otps.clazz = clazz;
        otps.type = RelationOptions.ONETOONE;

        this.relOpts.put(relationName, otps);

        return this;
    }

    protected DaoObject hasMany(String keyFrom, String keyTo, Class<?> clazz, String relationName) {

        RelationOptions otps = new RelationOptions();
        otps.opts.put("keyTo", keyTo);
        otps.opts.put("keyFrom", keyFrom);

        otps.clazz = clazz;
        otps.type = RelationOptions.ONETOMANY;

        this.relOpts.put(relationName, otps);

        return this;
    }

    protected DaoObject hasMany(String nearKey, String farKey, Class<?> clazz) {
        return this.hasMany(nearKey, farKey, clazz, clazz.getSimpleName());
    }

    protected DaoObject hasManyToMany(String keyFrom, String middleFrom, Class<?> middle, String middleTo, String keyTo, Class<?> result, String relationName) {

        RelationOptions otps = new RelationOptions();
        otps.opts.put("keyTo", keyTo);
        otps.opts.put("keyFrom", keyFrom);
        otps.opts.put("middleTo", middleTo);
        otps.opts.put("middleFrom", middleFrom);

        otps.clazz = result;
        otps.middle = middle;
        otps.type = RelationOptions.MANYTOMANY;

        this.relOpts.put(relationName, otps);

        return this;
    }

    protected DaoObject hasOne(String nearKey, String farKey, Class<?> clazz) {
        return this.hasOne(nearKey, farKey, clazz, clazz.getSimpleName());
    }

    public <T extends DaoObject> T getOne(String name) throws DaoObjectException {

        if (this.relOpts.containsKey(name)) {
            RelationOptions opts = this.relOpts.get(name);
            if (opts.type != RelationOptions.ONETOONE) {
                throw new DaoObjectException("There is no such relation type (one to one) on class " + this.getClass().getSimpleName());
            }
        } else {
            throw new DaoObjectException("There is no such relation `" + name + "` on class " + this.getClass().getSimpleName());
        }

        try {
            if (this.get(name).size() > 0) {
                return (T) this.get(name).get(0);
            } else {
                return null;
            }
        } catch (DaoObjectException e) {
            System.out.println("error getting relative info `" + name + "` for class `" + this.getClass().getSimpleName() + "`:" + e.getMessage());
            return null;
        }
    }

    public <T extends DaoObject> Vector<T> get(String name) throws DaoObjectException {

        // wet code
        if (!this.relOpts.containsKey(name)) {
            throw new DaoObjectException("There is no such relation `" + name + "` on class " + this.getClass().getSimpleName());
        }

        try {
            RelationOptions opts = this.relOpts.get(name);

            T resultClass = (T) opts.clazz.newInstance();
            if (!this.relations.containsKey(name)) {
                Field fromField;
                fromField = this.getClass().getField(opts.opts.get("keyFrom"));

                String hardcodedQ = "";

                switch (opts.type) {
                    case RelationOptions.ONETOMANY:
                    case RelationOptions.ONETOONE:

                        hardcodedQ = "SELECT * FROM " + resultClass.TableName() + " WHERE `" + opts.opts.get("keyTo") + "` = \"" + fromField.get(this) + "\"";
                        break;
                    case RelationOptions.MANYTOMANY:

                        DaoObject mClass = (DaoObject) opts.middle.newInstance();

                        hardcodedQ = "SELECT * FROM " + resultClass.TableName() + " as rTable "
                                + " JOIN " + mClass.TableName() + " as mTable ON mTable." + opts.opts.get("middleTo") + " = rTable." + opts.opts.get("keyTo")
                                + " WHERE mTable.`" + opts.opts.get("middleFrom") + "` = \"" + fromField.get(this) + "\"";
                        break;
                }

                System.out.println("RELATIONS: put `" + name + "` to class `" + this.getClass().getSimpleName() + "`");
                this.relations.put(name, db.executeRawQuery(hardcodedQ));
            }

            return this.relations.get(name).parseObjects(resultClass);
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
        } catch (ExecutingQueryException ex) {
            System.out.println(ex.getMessage());
            throw new DaoObjectException("Error while getting relative data for entity `" + this.getClass().getSimpleName() + "`, probably something wrong with setup method or entity class config");
        }

        return null;
    }

    public void setup() {

    }

    private void grabFromCache(CacheItem cache, int primary) throws DaoObjectException {
        DaoObject _cache = cache.data._cacheParsed.get(primary);
        if (_cache == null) {
            throw new DaoObjectException("Cache error: no such item in cache. Try to disable caching");
        }
        for (Entry<String, FieldInfo> f : this.FIELDS.entrySet()) {
            Field _field;
            try {
                _field = this.getClass().getField(f.getValue().name);
                _field.set(this, _field.get(_cache));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public DaoObject(int primaryKey) throws DaoObjectException {
        this.initialize();

        CacheItem _cache = null;
        if (Dao.useCache && Dao.cachedTables.containsKey(this.TableName())) {
            _cache = Dao.cache.get(Dao.cachedTables.get(this.TableName()));
        }

        if (_cache != null && _cache.valid()) {
            this.grabFromCache(_cache, primaryKey);
        } else {
            HashMap<String, String> primary = new HashMap<String, String>();
            primary.put(this.PrimaryKey(), String.valueOf(primaryKey));
            this.fill(primary);

            this.loadRelationsIfLazy();

            this.parseColumns();
        }
    }

    public DaoObject(HashMap<String, String> data) throws DaoObjectException {
        this.initialize();
        this.fill(data);

        this.loadRelationsIfLazy();
    }

    public void remove() throws DaoObjectException {
        StringBuilder sql = new StringBuilder();
        try {
            Field pKeyField = this.getClass().getField(this._primaryKey);
            Object pKeyVal = pKeyField.get(this);

            if (!pKeyVal.toString().equals("") && !pKeyVal.toString().equals("0")) {
                this._isLoaded = true;
            }
            if (this._isLoaded) {
                sql.append("DELETE FROM " + this._tableName + " WHERE " + this._primaryKey + " = '" + pKeyVal + "'");

                int _oldLifetime = Dao.cacheLifetime;
                Dao.cacheLifetime = 2;
                System.out.println("db: del " + this.getClass().getSimpleName() + "#" + this.getPrimaryKeyValue());
                try {
                    for (Entry<String, RelationOptions> it : this.relOpts.entrySet()) {
                        Vector<DaoObject> objs = this.get(it.getKey());
                        if (objs != null) {
                            for (DaoObject obj : this.get(it.getKey())) {
                                obj.remove();
                            }
                        }
                    }
                    db.executeRawQuery(sql.toString());
                } catch (Exception e) {
                }

                Dao.cacheLifetime = _oldLifetime;

                if (Dao.useCache) {
                    if (Dao.cachedTables.containsKey(this.TableName())) {
                        CacheItem _cache = Dao.cache.get(Dao.cachedTables.get(this.TableName()));

                        String primary = pKeyVal.toString();
                        for (int k = 0; k < _cache.data.data.size(); k++) {
                            if (primary == _cache.data.data.get(k).get(this._primaryKey)) {
                                _cache.data.data.remove(k);
                                break;
                            }
                        }
                    }
                }

                pKeyField.set(this, 0);
            } else {
                throw new DaoObjectException("Can't delete unloaded objects!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DaoObjectException("Please review column types and PrimaryKey()/TableName():");
        }
    }

    public void save() throws DaoObjectException {
        StringBuilder sql = new StringBuilder();
        boolean actualyLoaded = this._isLoaded;
        boolean isNew = false;

        HashMap<String, String> _forCache = new HashMap<String, String>();

        if (this.FIELDS.size() > 0) {
            try {
                StringBuilder fieldsValues = new StringBuilder();

                for (Entry<String, FieldInfo> f : this.FIELDS.entrySet()) {
                    Field field = this.getClass().getField(f.getValue().name);
                    String fieldType = field.getType().getCanonicalName();

                    if (f.getValue().timestamp) {
                        if (!fieldType.equals("java.util.Date")) {
                            throw new DaoObjectException("Timestamp fields should be instance of java.util.Date." + fieldType + " given");
                        }
                        field.set(this, new Date(System.currentTimeMillis()));
                    }

                    Object value = null;
                    // switch case must be here
                    if (fieldType.equals("java.util.Date")) {
                        Date tmp = (Date) field.get(this);
                        value = tmp.getTime();
                    } else {
                        value = field.get(this);
                    }

                    if (f.getValue().required && (value.toString().equals("") || value.toString().equals("0"))) {
                        throw new DaoObjectException("Not all required fields are filled");
                    }

                    if (Dao.useCache) {
                        _forCache.put(f.getKey(), value.toString());
                    }

                    fieldsValues.append("`" + f.getKey() + "` = '" + value + "',");
                }
                fieldsValues.deleteCharAt(fieldsValues.length() - 1);

                Field pKeyField = this.getClass().getField(this._primaryKey);
                Object pKeyVal = pKeyField.get(this);

                if (!pKeyVal.toString().equals("") && !pKeyVal.toString().equals("0")) {
                    this._isLoaded = true;
                }

                if (this._isLoaded) {
                    sql.append("UPDATE ");
                } else {
                    sql.append("INSERT INTO ");
                    isNew = true;
                }
                sql.append(this._tableName + " SET " + fieldsValues);
                if (this._isLoaded) {
                    sql.append(" WHERE " + this._primaryKey + " = '" + pKeyField.get(this) + "'");
                }

                int _oldLifetime = Dao.cacheLifetime;
                Dao.cacheLifetime = 2;
                DaoResult queryResult = db.executeRawQuery(sql.toString());
                Dao.cacheLifetime = _oldLifetime;

                if (!actualyLoaded && !queryResult.cached) {
                    int id = Integer.parseInt(queryResult.data().get(0).get("GENERATED_KEY"));
                    pKeyField.set(this, id);
                }

                if (Dao.useCache) {
                    if (Dao.cachedTables.containsKey(this.TableName())) {
                        CacheItem _cache = Dao.cache.get(Dao.cachedTables.get(this.TableName()));
                        if (isNew) {
                            _cache.data.data.add(_forCache);
                        } else {
                            String primary = pKeyVal.toString();
                            for (int k = 0; k < _cache.data.data.size(); k++) {
                                if (primary == _cache.data.data.get(k).get(this._primaryKey)) {
                                    _cache.data.data.set(k, _forCache);
                                    break;
                                }
                            }
                        }
                        _cache.lifetime = Dao.cacheLifetime;
                        _cache.fetched = System.currentTimeMillis();
                    }
                }

            } catch (DaoObjectException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace(); //new DaoObjectException("DaoObject misconfiguration: please review column types:"+e.getLocalizedMessage());
            }

        } else {
            System.out.println("Nothing to save");
        }
    }

    public String TableName() {

        if (this._tableName.equals("")) {
            if (this.getClass().isAnnotationPresent(Table.class)) {
                this._tableName = this.getClass().getAnnotation(Table.class).value();
                if (this._tableName.equals("")) {
                    this._tableName = this.getClass().getSimpleName().toLowerCase();
                }
            } else {
                this._tableName = this.getClass().getSimpleName().toLowerCase();
            }
        }

        return this._tableName;
    }

    /**
     * Excute only after parseFeildsInfo()
     *
     * @return
     */
    public String PrimaryKey() {

        if (this._primaryKey.equals("")) {
            this._primaryKey = DaoObject.DEFAULT_PRIMARY;
        }

        return this._primaryKey;
    }

    private int getPrimaryKeyValue() {
        try {
            Field pKey = this.getClass().getField(this.PrimaryKey());
            return (int) pKey.get(this);
        } catch (Exception ex) {
            return 0;
        }
    }

    private void loadRelationsIfLazy() {

        if (this.getClass().isAnnotationPresent(Table.class)) {
            if (this.getClass().getAnnotation(Table.class).lazy()) {

                // only for sql dbs
                // and for ints :)
                if (this.getPrimaryKeyValue() > 0) {
                    // auto fetching of related objects
                    for (Entry<String, RelationOptions> it : this.relOpts.entrySet()) {
                        try {
                            this.get(it.getKey());
                        } catch (DaoObjectException ex) {
                            System.out.println("error in initialization related objs fetched");
                        }
                    }
                }

            }
        }
    }

    public void fill(HashMap<String, String> data) throws DaoObjectException {
        for (Entry<String, String> f : data.entrySet()) {
            if (this.FIELDS.containsKey(f.getKey())) {
                FieldInfo fInfo = this.FIELDS.get(f.getKey());
                try {
                    Field _field = this.getClass().getField(fInfo.name);
                    String _fieldType = fInfo.type;
                    Object _fieldValue = null;
                    String columnValue = f.getValue();

                    switch (_fieldType) {
                        case "int":
                            _fieldValue = Integer.parseInt(columnValue);
                            break;
                        case "float":
                            _fieldValue = Float.parseFloat(columnValue);
                            break;
                        case "double":
                            _fieldValue = Double.parseDouble(columnValue);
                            break;
                        case "long":
                            _fieldValue = Long.parseLong(columnValue);
                            break;
                        case "java.lang.String":
                            _fieldValue = columnValue;
                            break;
                        case "java.util.Date":
                            _fieldValue = new Date(Long.parseLong(columnValue));
                            break;
                    }

                    _field.set(this, _fieldValue);
                } catch (NumberFormatException e) {
                    throw new DaoObjectException("Error parsing '" + f.getKey() + "' column. Is type of column right in " + this.getClass().getSimpleName() + "?");
                } catch (Exception e) {
                    throw new DaoObjectException("No field '" + f.getKey() + "' found while filling " + this.getClass().getSimpleName());
                }
            } else if (this.isStrict()) {
                throw new DaoObjectException("Trying to assign non exist field '" + f.getKey() + "' while initializing object of class `" + this.getClass().getSimpleName() + "`");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        try {
            for (Entry<String, FieldInfo> f : this.FIELDS.entrySet()) {
                Field _field = this.getClass().getField(f.getValue().name);
                result.append(f.getValue().name + " = " + _field.get(this) + ",");
            }
        } catch (Exception e) {
            System.out.println(this.getClass().getSimpleName());
            e.printStackTrace();
            System.out.println("some problems with output a DaoObject");
        }

        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private boolean isStrict() {
        if (this.getClass().isAnnotationPresent(Table.class)) {
            Table tA = this.getClass().getAnnotation(Table.class);
            return tA.strict();
        }
        return false;
    }

    private void parseFieldsInfo() {

        if (this.FIELDS.isEmpty()) {
            Field[] fields = this.getClass().getFields();
            for (Field m : fields) {
                if (Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers())) {
                    String colName = m.getName();
                    FieldInfo info = new FieldInfo();
                    if (m.isAnnotationPresent(Column.class)) {
                        Column colAnn = m.getAnnotation(Column.class);
                        info.required = colAnn.required();
                        info.timestamp = colAnn.timestamp();
                        if (!colAnn.value().equals("")) {
                            colName = colAnn.value();
                        }
                        if (colAnn.primary()) {
                            this._primaryKey = colName;
                        }
                    }
                    info.name = m.getName();
                    info.type = m.getType().getName();
                    this.FIELDS.put(colName, info);
                }
            }
        }
    }

    private void initialize() {
        this.parseFieldsInfo();
        this._tableName = this.TableName();
        this._primaryKey = this.PrimaryKey();
        this.setup();
    }

    private void parseColumns() throws DaoObjectException {
        try {
            Field pKeyField = this.getClass().getField(this.PrimaryKey());
            String sql = "SELECT * FROM " + this._tableName + " WHERE " + this._primaryKey + " = '" + pKeyField.get(this) + "'";
            this.fill(db.executeRawQuery(sql).data().get(0));
            this._isLoaded = true;
        } catch (NoSuchFieldException e) {
            throw new DaoObjectException("Primary key not found or misconfirated");
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new DaoObjectException("Error parsing a data from database. Perhaps such data not exists");
        } catch (IndexOutOfBoundsException e) {
            throw new DaoObjectException("Error parsing a data from database. MySQl returned 0 rows");
        } catch (ExecutingQueryException ex) {
            throw new DaoObjectException("Error configuring entity class `" + this.getClass().getSimpleName() + "`, check the config");
        }
    }
};
