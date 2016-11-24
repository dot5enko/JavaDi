package com.dot5enko.database;

import java.sql.ResultSet;

abstract public class AbstractDataProvider {
	
	abstract public DaoResult execute(String query);
	
}
