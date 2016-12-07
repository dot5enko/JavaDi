package com.dot5enko.database;

import com.dot5enko.database.exception.ExecutingQueryException;
import com.dot5enko.di.DependencyException;
import com.dot5enko.di.Instantiator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import org.bson.Document;

public final class MySQLProvider extends AbstractDataProvider {

    private Connection con;
    private final boolean debug;

    // ServiceContainer magic
    private static Document config;

    public static void setOptions(Document opts) {
        config = opts;
    }

    public MySQLProvider() throws SQLException, DependencyException {
        con = DriverManager.getConnection(config.getString("dsn"), config.getString("user"), config.getString("password"));
        debug = (boolean) config.getOrDefault("debug", false);
    }

    @Override
    public DaoResult execute(String query) throws ExecutingQueryException {
        try {
            java.sql.PreparedStatement s = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            if (debug) {
                System.out.println("SQL >" + query);
            }
            
            s.execute();

            ResultSet raw = s.getResultSet();
            if (raw == null) {
                raw = s.getGeneratedKeys();
            }

            ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
            try {

                ResultSetMetaData rsMetaData = raw.getMetaData();
                int columnCount = rsMetaData.getColumnCount();

                while (raw.next()) {

                    HashMap<String, String> row = new HashMap<String, String>();

                    for (int i = 0; i < columnCount; i++) {
                        String columnName = rsMetaData.getColumnName(i + 1);
                        row.put(columnName, raw.getString(columnName));

                    }

                    data.add(row);
                }

            } catch (SQLException e) {
                // that's mean empty result
            }

            return new DaoResult(data);
        } catch (SQLException e) {
            throw new ExecutingQueryException("Can't execute query:" + e.getMessage());
        }
    }
}
