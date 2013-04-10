package test.com.grahammueller.supermodel;

import static org.junit.Assert.*;
import org.junit.Test;
import com.grahammueller.supermodel.entity.AttributeType;

public class AttributeTypeDriver {
    @Test
    public void testAttributeTypeToJavaString() {
        assertEquals("boolean", AttributeType.BOOLEAN.toJavaString());
        assertEquals("String", AttributeType.BLOB.toJavaString());
        assertEquals("Date", AttributeType.DATE.toJavaString());
        assertEquals("double", AttributeType.DOUBLE.toJavaString());
        assertEquals("float", AttributeType.FLOAT.toJavaString());
        assertEquals("int", AttributeType.INTEGER.toJavaString());
        assertEquals("long", AttributeType.LONG.toJavaString());
        assertEquals("String", AttributeType.STRING.toJavaString());
        assertEquals("", AttributeType.UNDEFINED.toJavaString());
    }

    @Test
    public void testAttributeTypeToSQLiteString() {
        assertEquals("NUMERIC", AttributeType.BOOLEAN.toSQLiteString());
        assertEquals("BLOB", AttributeType.BLOB.toSQLiteString());
        assertEquals("TEXT", AttributeType.DATE.toSQLiteString());
        assertEquals("NUMERIC", AttributeType.DOUBLE.toSQLiteString());
        assertEquals("NUMERIC", AttributeType.FLOAT.toSQLiteString());
        assertEquals("NUMERIC", AttributeType.INTEGER.toSQLiteString());
        assertEquals("NUMERIC", AttributeType.LONG.toSQLiteString());
        assertEquals("TEXT", AttributeType.STRING.toSQLiteString());
        assertEquals("", AttributeType.UNDEFINED.toSQLiteString());
    }
}
