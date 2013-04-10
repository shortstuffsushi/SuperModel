package com.grahammueller.supermodel.gen.sqliteorm;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.grahammueller.supermodel.entity.Attribute;
import com.grahammueller.supermodel.entity.AttributeType;
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;
import com.grahammueller.supermodel.entity.Relationship;
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
     *         IllegalArgumentException On Attribute with UNDEFINED type
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
     *         IllegalArgumentException On Attribute with UNDEFINED type
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
     * @throws IllegalArgumentException On Attribute with UNDEFINED type
     */
    private static void generateTables() throws IllegalArgumentException {
        List<Entity> entities = EntityManager.getAllEntities();
        Map<Entity, StringBuilder> tableBuilders = new HashMap<Entity, StringBuilder>();

        // Iterate first time to generate basic table fields
        for (Entity entity : entities) {
            tableBuilders.put(entity, createEntityTableBuilder(entity));
        }

        // Iterate again to handle relationships
        for (Entity entity : entities) {
            createRelationshipColumns(entity, tableBuilders);
        }

        // Finally actually generate tables
        generateTables(tableBuilders);
    }

    /**
     * Generates the table for a specific Entity
     * @param entity The Entity whose table will be generated
     * @throws IllegalArgumentException On Attribute with UNDEFINED type
     */
    private static StringBuilder createEntityTableBuilder(Entity entity) throws IllegalArgumentException {
        StringBuilder tableBuilder = new StringBuilder(String.format(CREATE_TABLE_FORMAT, entity.getName().toLowerCase()));

        // Build up attribute strings
        for (Attribute attr : entity.getAttributes()) {
            if (attr.getType() == AttributeType.UNDEFINED) {
                throw new IllegalArgumentException(attr.getName() + " does not have a valid type");
            }

            if (attr.isPrimaryKey()) {
                tableBuilder.append(String.format(ATTR_COLUMN_DEFN_FORMAT, attr.getName(), PRIMARY_KEY_DEFN)).append(",\n");
            }
            else {
                tableBuilder.append(String.format(ATTR_COLUMN_DEFN_FORMAT, attr.getName(), attr.getType().toSQLiteString())).append(",\n");
            }
        }

        //Drop last newline and comma, append parenthesis
        tableBuilder.delete(tableBuilder.length() - 2, tableBuilder.length() - 1).append(");");

        return tableBuilder;
    }

    /**
     * Generates the extra columns for Relationships
     * @param entity The Entity whose Relationships will be parsed
     * @param tableBuilders The Map of StringBuilders to edit
     */
    private static void createRelationshipColumns(Entity entity, Map<Entity, StringBuilder> tableBuilders) {
        for (Relationship rltn : entity.getRelationships()) {
            StringBuilder rltnBuilder = tableBuilders.get(rltn.getEntity());

            Attribute primaryKey = entity.getPrimaryKey();
            String mergedNameAndKey = entity.getName().toLowerCase() + primaryKey.getName().substring(0, 1).toUpperCase() + primaryKey.getName().substring(1);

            rltnBuilder.insert(rltnBuilder.length() - 3, ",\n" + String.format(ATTR_COLUMN_DEFN_FORMAT, mergedNameAndKey, primaryKey.getType().toSQLiteString()));
        }
    }

    /**
     * Actual generates the tables in the database
     * @param tableBuilders The Map of StringBuilders to use to execute the queries
     */
    private static void generateTables(Map<Entity, StringBuilder> tableBuilders) {
        for (Entry<Entity, StringBuilder> builder : tableBuilders.entrySet()) {
            try {
                Statement stmt = connection.createStatement();

                // Drop any pre-existing table
                stmt.execute(String.format(DROP_TABLE_FORMAT, builder.getKey().getName().toLowerCase()));

                // Create entity's table
                stmt.execute(builder.getValue().toString());
            }
            catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

     private static Connection connection;

    // Constants
    private static final String PRIMARY_KEY_DEFN = "INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String DROP_TABLE_FORMAT = "drop table if exists %s;\n";
    private static final String CREATE_TABLE_FORMAT = "create table %s (\n";
    private static final String ATTR_COLUMN_DEFN_FORMAT = "  %s %s";
}
