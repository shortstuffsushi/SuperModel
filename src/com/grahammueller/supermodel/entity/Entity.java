package com.grahammueller.supermodel.entity;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    protected String _name;
    protected List<Attribute> _attributes;
    protected List<Relationship> _relationships;

    /**
     * Default constructor for an Entity
     * 
     * @param name Name of the Entity. Should not contain characters, or start with a number.
     * @throws IllegalArgumentException Invalid Entity Name specified
     */
    public Entity(String name) throws IllegalArgumentException {
        EntityManager.validateName(name, "Entity");

        _name = name;
        _attributes = new ArrayList<Attribute>();
        _relationships = new ArrayList<Relationship>();

        // Attempt to register, throw exception on failure
        // which indicates another entity with this name exists
        if (!EntityManager.registerEntity(this)) { throw new IllegalArgumentException("Unable to register Entity"); }
    }

    /**
     * Gets the name of the Entity.
     * 
     * @return The Entity's name
     */
    public String getName() {
        return _name;
    }

    /**
     * Attempts to update Entity name, if it is valid and no Entity already exists with the same name
     * 
     * @param name The new name
     * @throws IllegalArgumentException Invalid Entity Name specified
     * @return Whether set was successful
     */
    public boolean setName(String name) throws IllegalArgumentException {
        return EntityManager.updateEntityName(this, name);
    }

    /**
     * Method for adding attributes to an Entity
     * 
     * @param name The attribute name. Follows same naming convention as an Entity.
     * @param type The type of the attribute. Can be any of the Attribute.Type Enum values.
     * @throws IllegalArgumentException Invalid Attribute Name specified
     * @return Whether the attribute was added
     */
    public void addAttribute(String name, AttributeType type) throws IllegalArgumentException {
        _attributes.add(new Attribute(name, type));
    }

    /**
     * Attempts to get and set an attribute's name
     * 
     * @param oldName The desired attribute's name
     * @param newName The new name for that attribute
     * @throws IllegalArgumentException Invalid Attribute Name specified
     * @return Whether set was successful
     */
    public boolean updateAttributeName(String oldName, String newName) {
        Attribute storedAttr = null;

        for (Attribute attr : _attributes) {
            // Found stored attribute
            if (attr.getName().equals(oldName)) {
                storedAttr = attr;
            }

            // Attribute name is in use
            if (attr.getName().equals(newName)) {
                return false;
            }
        }

        // No attribute with old name found
        if (storedAttr == null) {
            return false;
        }

        storedAttr.setName(newName);

        return true;
    }

    /**
     * Attempts to set an attribute's type
     * 
     * @param name The desired attribute's name
     * @param type The new type for that attribute
     * @throws IllegalArgumentException If trying to set a primary key with one previously specified
     * @return Whether set was successful
     */
    public boolean updateAttributeType(String name, AttributeType type) {
        Attribute storedAttr = null;
        boolean hasPrimaryKey = false;

        for (Attribute attr : _attributes) {
            // Found stored attribute
            if (attr.getName().equals(name)) {
                storedAttr = attr;
            }

            // Already has an Integer Primary Key
            if (attr.getType() == AttributeType.INTEGER_PRIMARY_KEY) {
                hasPrimaryKey = true;
            }
        }

        if (storedAttr == null) {
            return false;
        }

        if (type == AttributeType.INTEGER_PRIMARY_KEY && hasPrimaryKey) {
            throw new IllegalArgumentException("Already has primary key");
        }

        storedAttr.setType(type);

        return true;
    }

    /**
     * Gets the attributes.
     * TODO this should probably be returning a clone/readonly version
     * 
     * @return The Entity's attributes
     */
    public List<Attribute> getAttributes() {
        return _attributes;
    }

    /**
     * Method for adding relationships to an Entity
     * 
     * @param name The relationship name. Follows same naming convention as an Entity.
     * @param type The Entity name of the linked Entity. Can be anything, but when validation
     *             is run, it will check to see that an Entity with the name exists
     * @throws IllegalArgumentException Invalid Relationship Name specified
     * @return Whether the attribute was added
     */
    public boolean addRelationship(String name, String entity) throws IllegalArgumentException {
        EntityManager.validateName(name, "Relationship");

        _relationships.add(new Relationship(name, entity));

        return true;
    }

    /**
     * Attempts to get and set an attribute's name
     * 
     * @param oldName The desired attribute's name
     * @param newName The new name for that attribute
     * @throws IllegalArgumentException Invalid Attribute Name specified
     * @return Whether set was successful
     */
    public boolean updateRelationshipName(String oldName, String newName) {
        Relationship storedRelationship = null;

        for (Relationship relationship : _relationships) {
            // Found stored attribute
            if (relationship.getName().equals(oldName)) {
                storedRelationship = relationship;
            }

            // Attribute name is in use
            if (relationship.getName().equals(newName)) {
                return false;
            }
        }

        // No attribute with old name found
        if (storedRelationship == null) {
            return false;
        }

        storedRelationship.setName(newName);

        return true;
    }

    /**
     * Gets the relationships. TODO this should probably be returning a clone/readonly version
     * 
     * @return The Entity's relationships
     */
    public List<Relationship> getRelationships() {
        return _relationships;
    }

    /**
     * @return This Entity in string form
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(_name).append('$');

        for (Attribute attribute : _attributes) {
            sb.append(attribute.getName()).append(':').append(attribute.getType().name()).append('#');
        }

        sb.append("$");
        for (Relationship relationship : _relationships) {
            sb.append(relationship.getName()).append(':').append(relationship.getValue()).append('#');
        }

        return sb.toString();
    }

    /**
     * Generates an Entity from a formatted string
     * 
     * @param entityText The formatted Entity string Should be of form NAME$ATTR1NAME:TYPE#ATTR2NAME:TYPE#
     * @return A new Entity
     * @throws IllegalArgumentException Improperly named Entity, Attributes, or poorly formatted string
     */
    public static Entity fromString(String entityText) throws IllegalArgumentException {
        Entity retEnt = null;

        entityText = entityText.replace("$$", "$ $").replaceFirst("\\$$", "\\$ ");
        String[] pieces = entityText.split("\\$");

        retEnt = new Entity(pieces[0]);

        if (pieces.length != 3) {
            throw new IllegalArgumentException("Entity malformed");
        }

        String[] attributes = pieces[1].split("#");
        String[] relationships = pieces[2].split("#");

        for (String attribute : attributes) {
            // Remove leading and trailing spaces
            attribute = attribute.trim();

            // Skip empty attributes
            if (attribute.isEmpty()) {
                continue;
            }

            String[] attrPieces = attribute.split(":");

            if (attrPieces.length != 2) {
                throw new IllegalArgumentException("Attribute malformed");
            }

            retEnt.addAttribute(attrPieces[0], AttributeType.valueOf(attrPieces[1]));
        }

        for (String relationship : relationships) {
            // Remove leading and trailing spaces
            relationship = relationship.trim();

            // Skip empty relationships
            if (relationship.isEmpty()) {
                continue;
            }

            String[] relPieces = relationship.split(":");

            // Report malformed attributes
            if (relPieces.length != 2) {
                throw new IllegalArgumentException("Relationship malformed");
            }

            retEnt.addRelationship(relPieces[0], relPieces[1]);
        }

        return retEnt;
    }

    /**
     * Determines equality based on name.
     * 
     * @param o The other object
     * @return Whether objects are equal
     */
    public boolean equals(Object o) {
        return (o instanceof Entity) && _name.equals(((Entity) o).getName());
    }
}
