package com.grahammueller.supermodel.entity;

/**
 * This enum represents the SQLite data types
 * that an attribute can be mapped to.
 */
public enum AttributeType {
    INTEGER_PRIMARY_KEY, NUMERIC, TEXT, BLOB; 

    /**
     * Gets the string version of the type,
     * replacing underscore with space
     * 
     * @return This, in string form
     */
    public String toString() {
        return name().replace('_', ' ');
    }

    /**
     * Gets the string for the Java class
     * name that this type represents
     * @return This, in Java class name form
     */
    public String toJavaString() {
        switch (this) {
            case INTEGER_PRIMARY_KEY : return "long";
            case NUMERIC : return "double";
            case TEXT :
            case BLOB : return "String";
            default : return "";
        }
    }

    /**
     * Gets the string for the SQL column definition
     * @return This, in SQL column definition form
     */
    public String toSQLString() {
        switch (this) {
            case INTEGER_PRIMARY_KEY : return "INTEGER PRIMARY KEY AUTOINCREMENT";
            case NUMERIC :
            case TEXT :
            case BLOB :
            default : return toString();
        }
    }
}
