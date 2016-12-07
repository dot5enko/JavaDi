package com.dot5enko.database;

public interface WhereClause<T extends DaoObject>  {
	abstract public boolean compare(T row);
}