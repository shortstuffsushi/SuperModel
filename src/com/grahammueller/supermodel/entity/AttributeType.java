package com.grahammueller.supermodel.entity;

/**
 * This enum represents the available data
 * types that an Attribute can be mapped to.
 */
public enum AttributeType {
    BLOB, BOOLEAN, DATE, DOUBLE, FLOAT, INTEGER, LONG, STRING, UNDEFINED;

    /**
     * Gets the string for the Java class
     * name that this type represents
     * @return This, in Java class name form
     */
    public String toJavaString() {
        switch (this) {
            case BOOLEAN : return "boolean";
            case BLOB : return "String";
            case DATE : return "Date";
            case DOUBLE : return "double";
            case FLOAT : return "float";
            case INTEGER : return "int";
            case LONG : return "long";
            case STRING : return "String";
            default : return "";
        }
    }

    /**
     * Gets the string for the SQLite column definition
     * @return This, in SQLite column definition form
     */
    public String toSQLiteString() {
        switch (this) {
            case BOOLEAN : case DOUBLE :case FLOAT : case INTEGER : case LONG :
                return "NUMERIC";
            case DATE : case STRING :
                return "TEXT";
            case BLOB :
                return "BLOB";
            default : return "";
        }
    }
}
