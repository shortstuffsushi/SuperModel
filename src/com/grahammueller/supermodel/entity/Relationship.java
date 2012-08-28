package com.grahammueller.supermodel.entity;

public class Relationship {
    // TODO
    public Relationship(String name, String value) {
      _name = name;
      _value = value;
    }
  
    public String getName() {
        return _name;
    }
    
    public void setName(String name) {
        _name = name; 
    }
    
    public String getValue() {
        return _value;
    }
    
    public void setValue(String value) {
        _value = value;
    }
  
    private String _name;
    private String _value;
}