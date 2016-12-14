package com.dot5enko.database;

import com.dot5enko.database.exception.DaoObjectException;
import com.dot5enko.database.exception.ExecutingQueryException;
import com.dot5enko.di.ClassFinder;
import com.dot5enko.di.annotation.InjectInstance;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import org.bson.Document;

public class Dao {

    public static class RelationOptions {

        final static int ONETOONE = 1;
        final static int ONETOMANY = 2;
        final static int MANYTOMANY = 3;

        HashMap<String, String> opts = new HashMap();

        Class<?> clazz;
        Class<?> middle;

        int type;
    }

    class CacheItem {

        public int lifetime; // seconds
        public long fetched;
        public DaoResult data;

        public CacheItem() {
        }

        public boolean valid() {
            return (System.currentTimeMillis() - this.fetched) < (long) this.lifetime * 1000;
        }

        public CacheItem(DaoResult data) {
            this.data = data;
            this.fetched = System.currentTimeMillis();
            this.lifetime = Dao.cacheLifetime;
        }

    }
    
    public static HashMap<String, HashSet<String>> affectedEntitys = new HashMap();
    public static HashMap<String, HashMap<String, RelationOptions>> relations = new HashMap();

    private AbstractDataProvider provider;
    private WhereClause _wherecomp = new WhereAllRows();

    static boolean useCache = false;
    static int cacheLifetime = 1 * 30;
    static HashMap<String, CacheItem> cache = new HashMap<String, CacheItem>();
    static HashMap<String, String> cachedTables = new HashMap<String, String>();

    // ServiceContainer magic
    private static Document config;

    public static void setOptions(Document opts) {
        config = opts;
    }

    public Dao(@InjectInstance("database") AbstractDataProvider dataprovider) {
        this.provider = dataprovider;

        System.out.println("Dao initialized");

        RelationParser parser = new RelationParser(affectedEntitys);

        List<String> parsePackages = config.get("watch", List.class);
        for (String packageName : parsePackages) {

            System.out.println("Parsing db entitys in package " + packageName);

            for (Class<?> it : ClassFinder.find(packageName)) {
                if (it.getSuperclass() != null && it.getSuperclass().equals(DaoObject.class)) {
                    System.out.println("--->" + it.getCanonicalName() + "<->" + parser.getConfigurationForClass(it));
                    relations.put(it.getCanonicalName(), parser.getConfigurationForClass(it));
                }
            }
        }

    }

    public Dao Where(WhereClause whereClause) {
        this._wherecomp = whereClause;
        return this;
    }

    public <T extends DaoObject> Vector<T> find(T instance) throws DaoObjectException {
        return this.find(instance, 0);
    }

    public <T extends DaoObject> Vector<T> find(T instance, int limit) throws DaoObjectException {
        Vector<T> result = new Vector<T>();
        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM " + instance.TableName());
            if (limit > 0) {
                sql.append(" LIMIT " + limit);
            } else if (Dao.useCache) {
                Dao.cachedTables.put(instance.TableName(), Dao.md5Custom(sql.toString()));
            }

            result = this.executeRawQuery(sql.toString()).parseObjects(instance, this._wherecomp);
        } catch (Exception e) {
            throw new DaoObjectException(e.getMessage());
        }

        this._wherecomp = new WhereAllRows(); // clear where clause

        return result;
    }

    public static int cacheLifetime() {
        return Dao.cacheLifetime;
    }

    public static void setCacheLifetime(int seconds) {
        Dao.cacheLifetime = seconds;
    }

    public static void enableCache(boolean state) {
        Dao.useCache = state;
    }

    public DaoResult executeRawQuery(String q) throws ExecutingQueryException {
        DaoResult _data = null;

        if (Dao.useCache) {
            String hash = Dao.md5Custom(q);
            CacheItem _cache = null;
            if (Dao.cache.containsKey(hash)) {
                _cache = Dao.cache.get(hash);
                String _typeSql = q.trim().substring(0, 6).toLowerCase();
                if (!_typeSql.equals("select")) {
                    System.out.println("Dao cache: Similar non-select (" + q + ") query was executed " + (System.currentTimeMillis() - _cache.fetched) / 1000L + ". Is it not a bug?");
                }
            }

            if (_cache != null && _cache.valid()) {
                _data = _cache.data;
                _data.cached = true;
            } else {
                _data = this.provider.execute(q);
                Dao.cache.put(hash, new CacheItem(_data));
            }
        } else {
            _data = this.provider.execute(q);
        }
        return _data;
    }

    /**
     * another methods (shouldn't be there)
     */
    public static String md5Custom(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while (md5Hex.length() < 32) {
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }

}
