package com.grahammueller.supermodel.entity;

/**
 * An Attribute, or property, on an Entity.
 */
public class Attribute {
    /**
     * Creates an Attribute for an Entity
     *
     * @param type The attribute type
     * @param name The attribute name
     * @throws IllegalArgumentException Invalid Attribute Name specified
     */
    public Attribute(String name, AttributeType type) throws IllegalArgumentException {
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
    public AttributeType getType() {
        return _type;
    }

    /**
     * Attempts to set the attribute's type
     * @param type The type
     */
    public void setType(AttributeType type) {
        _type = type;
    }

    private String _name;
    private AttributeType _type;
}
