package com.dot5enko.database;

public class WhereAllRows extends WhereClause {

	@Override
	public boolean compare(DaoObject row) {
		return true;
	}
	
}
