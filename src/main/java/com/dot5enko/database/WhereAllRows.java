package com.dot5enko.database;

public class WhereAllRows implements WhereClause {

	@Override
	public boolean compare(DaoObject row) {
		return true;
	}
	
}
