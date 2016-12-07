package com.dot5enko.database;

import com.dot5enko.database.exception.ExecutingQueryException;
import java.sql.ResultSet;

abstract public class AbstractDataProvider {
	
	abstract public DaoResult execute(String query) throws ExecutingQueryException;
	
}
