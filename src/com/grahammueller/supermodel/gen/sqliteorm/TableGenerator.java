package com.grahammueller.supermodel.gen.sqliteorm;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import com.grahammueller.supermodel.entity.Attribute;
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;
import com.grahammueller.supermodel.gen.exception.ConnectionException;

/**
 * Table Generator for the SQLiteORM
 * @author gmueller
 */
public class TableGenerator {
    /**
     * Method used to generate Entity tables.
     * The current implementation will drop existing tables, if found.
     * 
     * @param pathToDatabase File location of database
     * @param databaseName Name of database to use
     * @throws ExceptionConnection Failure to load JDBC or connect to specified database
     */
    public static void generateTables(String pathToDatabase, String databaseName) throws ConnectionException {
        connectToDatabase(pathToDatabase, databaseName);

        generateTables();
    }

    /**
     * Instantiates the database connection
     * @param pathToDatabase URL/File path to database
     * @param databaseName Actual database name
     * @throws ExceptionConnection Failure to load JDBC or connect to specified database
     */
    private static void connectToDatabase(String pathToDatabase, String databaseName) throws ConnectionException {
        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e) {
            throw new ConnectionException("The JDBC driver couldn't be found, so a SQL connection couldn't be established.");
        }

        try {
            String connectionString = String.format("jdbc:sqlite:%s%s%s", pathToDatabase, File.separator, databaseName);
            connection = DriverManager.getConnection(connectionString);
            connection.setAutoCommit(true);
        }
        catch (SQLException e) {
            throw new ConnectionException("Unable to connect to database -- " + e.getLocalizedMessage());
        }
    }

    /**
     * Generates tables for all entities currently managed by the Entity Manager
     */
    private static void generateTables() {
        for (Entity e : EntityManager.getAllEntities()) {
            createEntityTable(e);
        }
    }

    /**
     * Generates the table for a specific Entity
     * @param e The Entity whose table will be generated
     */
    private static void createEntityTable(Entity e) {
        StringBuilder tableBuilder = new StringBuilder();

        tableBuilder.append(String.format(CREATE_TABLE_FORMAT, e.getName()));

        // Build up attribute strings
        for (Attribute attr : e.getAttributes()) {
            tableBuilder.append(String.format(ATTR_COLUMN_DEFN_FORMAT, attr.getName(), attr.getType().toSQLString()));
        }

        //Drop last newline and comma
        tableBuilder.delete(tableBuilder.length() - 2, tableBuilder.length() - 1);

        tableBuilder.append(");");

        try {
            Statement stmt = connection.createStatement();

            // Drop any pre-existing table
            stmt.execute(String.format(DROP_TABLE_FORMAT, e.getName()));

            // Create entity's table
            stmt.execute(tableBuilder.toString());
        }
        catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private static Connection connection;

    // Constants
    private static final String DROP_TABLE_FORMAT = "drop table if exists %s;\n";
    private static final String CREATE_TABLE_FORMAT = "create table %s (\n";
    private static final String ATTR_COLUMN_DEFN_FORMAT = "  %s %s,\n";
}
