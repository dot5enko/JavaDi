package com.dot5enko.database;

public abstract class WhereClause<T extends DaoObject>  {
	abstract public boolean compare(T row);
}