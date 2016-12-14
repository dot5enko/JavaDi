package com.dot5enko.database;

import com.dot5enko.database.annotations.*;
import java.util.HashMap;

public class RelationParser {

    // this is very bad. need to be static for Dao Class
    private HashMap<String, DaoObject.RelationOptions> relOpts = new HashMap();

    // relation methods
    private RelationParser hasOne(String keyFrom, String keyTo, Class<?> clazz, String relationName) {

        DaoObject.RelationOptions otps = new DaoObject.RelationOptions();
        otps.opts.put("keyTo", keyTo);
        otps.opts.put("keyFrom", keyFrom);

        otps.clazz = clazz;
        otps.type = DaoObject.RelationOptions.ONETOONE;

        this.relOpts.put(relationName, otps);

        return this;
    }

    private RelationParser hasMany(String keyFrom, String keyTo, Class<?> clazz, String relationName) {

        DaoObject.RelationOptions otps = new DaoObject.RelationOptions();
        otps.opts.put("keyTo", keyTo);
        otps.opts.put("keyFrom", keyFrom);

        otps.clazz = clazz;
        otps.type = DaoObject.RelationOptions.ONETOMANY;

        this.relOpts.put(relationName, otps);

        return this;
    }

    private RelationParser hasMany(String nearKey, String farKey, Class<?> clazz) {
        return this.hasMany(nearKey, farKey, clazz, clazz.getSimpleName());
    }

    private RelationParser hasManyToMany(String keyFrom, String middleFrom, Class<?> middle, String middleTo, String keyTo, Class<?> result, String relationName) {

        DaoObject.RelationOptions otps = new DaoObject.RelationOptions();
        otps.opts.put("keyTo", keyTo);
        otps.opts.put("keyFrom", keyFrom);
        otps.opts.put("middleTo", middleTo);
        otps.opts.put("middleFrom", middleFrom);

        otps.clazz = result;
        otps.middle = middle;
        otps.type = DaoObject.RelationOptions.MANYTOMANY;

        this.relOpts.put(relationName, otps);

        return this;
    }

    private RelationParser hasOne(String nearKey, String farKey, Class<?> clazz) {
        return this.hasOne(nearKey, farKey, clazz, clazz.getSimpleName());
    }

    public HashMap<String, DaoObject.RelationOptions> getConfigurationForClass(Class<?> clazz) {

        this.relOpts.clear();
        
        HasManyArray hasManyArray = clazz.getAnnotation(HasManyArray.class);
        if (hasManyArray != null) {
            for (HasMany it : hasManyArray.value()) {
                this.hasMany(it.from(), it.to(), it.value(), it.alias());
            }
        } else {
            HasMany hm = clazz.getDeclaredAnnotation(HasMany.class);
            if (hm != null) {
                this.hasMany(hm.from(), hm.to(), hm.value(),hm.alias());
            }
        }

        HasOneArray oneArray = clazz.getAnnotation(HasOneArray.class);
        if (oneArray != null) {
            for (HasOne it : oneArray.value()) {
                 this.hasOne(it.from(), it.to(), it.value(), it.alias());
            }
        }else {
            HasOne ho = clazz.getDeclaredAnnotation(HasOne.class);
            if (ho != null) {
                this.hasMany(ho.from(), ho.to(), ho.value(),ho.alias());
            }
        }

        HasManyToManyArray manyToManyArray = clazz.getAnnotation(HasManyToManyArray.class);
        if (manyToManyArray != null) {
            for (HasManyToMany it : manyToManyArray.value()) {
               this.hasManyToMany(it.from(), it.mediateFrom(), it.mediate(), it.mediateTo(), it.to(), it.value(), it.alias());
            }
        }else {
            HasManyToMany hmm = clazz.getDeclaredAnnotation(HasManyToMany.class);
            if (hmm != null) {
                this.hasManyToMany(hmm.from(), hmm.mediateFrom(),hmm.mediate(),hmm.mediateTo(),hmm.to(),hmm.value(),hmm.alias());
            }
        }
        
        return this.relOpts;
    }
}
