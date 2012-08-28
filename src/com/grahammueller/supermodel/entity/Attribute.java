package com.grahammueller.supermodel.entity;

public class Attribute {
    public enum Type { 
        INTEGER_PRIMARY_KEY, NUMERIC, TEXT, BLOB; 

        public String toString() {
            return name().replace('_', ' ');
        }
    }

    public Attribute() {
        this(null, null);
    }

    public Attribute(Attribute.Type type, String name) {
        _type = type;
        _name = name;
    }
    
    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }
    
    public Attribute.Type getType() {
        return _type;
    }
    
    public void setType(Attribute.Type type) {
        _type = type;
    }
    
    private String _name;
    private Attribute.Type _type;
}