package com.grahammueller.supermodel.entity;

public class Attribute {
    public enum Type { 
        INTEGER_PRIMARY_KEY, NUMERIC, TEXT, BLOB; 

        public String toString() {
            return name().replace('_', ' ');
        }
    }

    /**
     * Creates an Attribute for an Entity
     *
     * @param type The attribute type
     * @param name The attribute name
     * @throws IllegalArgumentException Invalid Attribute Name specified
     */
    public Attribute(String name, Attribute.Type type) throws IllegalArgumentException {
    	EntityManager.validateName(name, "Attribute");
 
        _type = type;
        _name = name;
    }
    
    /**
     * Gets the attribute's name
     * @return The name
     */
    public String getName() {
        return _name;
    }

    /**
     * Attempts to set the attribute's name
     * @param name The name
     * @throws IllegalArgumentException Invalid Attribute Name specified
     */
    public void setName(String name) {
    	EntityManager.validateName(name, "Attibute");

        _name = name;
    }

    
    /**
     * Gets the attribute's type
     * @return The type
     */
    public Attribute.Type getType() {
        return _type;
    }

    /**
     * Attempts to set the attribute's type
     * @param type The type
     */
    public void setType(Attribute.Type type) {
        _type = type;
    }
    
    private String _name;
    private Attribute.Type _type;
}