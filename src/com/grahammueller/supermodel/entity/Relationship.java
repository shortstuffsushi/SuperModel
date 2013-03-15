package com.grahammueller.supermodel.entity;

/**
 * A Relationship between Entities
 */
public class Relationship {
    /**
     * Creates a Relationship for an Entity. This does not
     * validate that the other Entity (the value) exists, in
     * case it has not yet been created.
     * 
     * @param name The name for the Relationship
     * @param value The type of the other Entity
     */
    public Relationship(String name, String value) {
      _name = name;
      _value = value;
    }

    /**
     * Gets the Relationship name
     * @return The name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the Relationship name
     * @param name The new name
     */
    public void setName(String name) {
        _name = name; 
    }

    /**
     * Gets the other Entity type
     * @return Other Entity type
     */
    public String getValue() {
        return _value;
    }

    /**
     * Sets the other Entity type
     * @param value Other Entity type
     */
    public void setValue(String value) {
        _value = value;
    }

    private String _name;
    private String _value;
}
