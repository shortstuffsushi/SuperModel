package com.grahammueller.supermodel.entity;

/**
 * An Attribute, or property, on an Entity.
 */
public class Attribute {
    /**
     * Creates an Attribute for an Entity
     * 
     * TODO Make this class immutable
     *
     * @param type The AttributeType
     * @param name The Attribute name
     * @throws IllegalArgumentException Invalid Attribute Name specified
     */
    public Attribute(String name, AttributeType type) throws IllegalArgumentException {
        EntityManager.validateName(name, "Attribute");

        _type = type;
        _name = name;
    }

    /**
     * Gets the Attribute's name
     * @return The name
     */
    public String getName() {
        return _name;
    }

    /**
     * Attempts to set the Attribute's name
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
     * Sets the Attribute's type
     * @param type The type
     */
    public void setType(AttributeType type) {
        _type = type;
    }

    /**
     * Gets whether the Attribute is a Primary Key
     * @return Whether this is a Primary Key
     */
    public boolean isPrimaryKey() {
        return _isPrimaryKey;
    }

    /**
     * Updates whether the Attribute is a Primary Key
     * @param isPrimaryKey Whether this is a Primary Key
     * @throws IllegalArgumentException Attribute is of invalid AttributeType to become a Primary Key
     */
    public void setPrimaryKey(boolean isPrimaryKey) throws IllegalArgumentException {
        if (isPrimaryKey && _type != AttributeType.INTEGER && _type != AttributeType.LONG) {
            throw new IllegalArgumentException("Specified Attribute Type cannot be Primary Key");
        }

        _isPrimaryKey = isPrimaryKey;
    }

    private String _name;
    private AttributeType _type;
    private boolean _isPrimaryKey;
}
