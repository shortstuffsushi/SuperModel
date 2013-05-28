package com.grahammueller.supermodel.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An Entity, represents a Plain Old Java Object, or POJO.
 * This Entity will be mapped to a Database table, allowing
 * for simple data storage and interaction.
 */
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

        // Attempt to register, which throws exception on failure
        EntityManager.registerEntity(this);
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
     */
    public void setName(String name) throws IllegalArgumentException {
        EntityManager.updateEntityName(this, name);
    }

    /**
     * Method for adding attributes to an Entity
     * 
     * @param name The attribute name. Follows same naming convention as an Entity.
     * @param type The type of the attribute. Can be any of the Attribute.Type Enum values.
     * @throws IllegalArgumentException Invalid Attribute Name specified
     */
    public void addAttribute(String name, AttributeType type) throws IllegalArgumentException {
        _attributes.add(new Attribute(name, type));
    }

    /**
     * Attempts to get and set an attribute's name
     * 
     * @param oldName The desired attribute's name
     * @param newName The new name for that attribute
     * @throws IllegalArgumentException Invalid Attribute name specified, name already in use, or existing Attribute not found.
     */
    public void updateAttributeName(String oldName, String newName) {
        Attribute storedAttr = null;
        for (Attribute attr : _attributes) {
            // Found stored attribute
            if (attr.getName().equals(oldName)) {
                storedAttr = attr;
            }

            // Attribute name is in use
            if (attr.getName().equals(newName)) {
                throw new IllegalArgumentException("Attribute name in use");
            }
        }

        // No attribute with old name found
        if (storedAttr == null) {
            throw new IllegalArgumentException("Requested Attribute not found");
        }

        storedAttr.setName(newName);
    }

    /**
     * Attempts to set an attribute's type
     * 
     * @param name The desired attribute's name
     * @param type The new type for that attribute
     * @throws IllegalArgumentException Attribute not found, or trying to set a primary key with one previously specified.
     */
    public void updateAttributeType(String name, AttributeType type) {
        for (Attribute attr : _attributes) {
            // Found stored attribute
            if (attr.getName().equals(name)) {
                attr.setType(type);

                // Force clear Primary Key if it is no longer applicable
                if (attr.getType() != AttributeType.INTEGER && attr.getType() != AttributeType.LONG) {
                    attr.setPrimaryKey(false);
                }

                return;
            }
        }

        throw new IllegalArgumentException("Requested Attribute not found");
    }

    /**
     * Gets the primary key Attribute.
     * 
     * @return The Entity's primary key Attribute or null
     */
    public Attribute getPrimaryKey() {
        for (Attribute attr : _attributes) {
            if (attr.isPrimaryKey()) {
                return attr;
            }
        }

        return null;
    }

    /**
     * Attempts to update an Attribute's Primary Key status
     * 
     * @param name The Attribute to update.
     * @param isPrimaryKey Whether the Attribute should be the Primary Key
     * @throws IllegalArgumentException Primary Key already exists, or Specified Attribute could not be found
     */
    public void setPrimaryKey(String name, boolean isPrimaryKey) throws IllegalArgumentException {
        if (getPrimaryKey() != null && isPrimaryKey) {
            throw new IllegalArgumentException("Already has a primary key");
        }

        for (Attribute attr : _attributes) {
            if (attr.getName().equals(name)) {
                attr.setPrimaryKey(isPrimaryKey);
                return;
            }
        }

        throw new IllegalArgumentException("Requested Attribute not found");
    }

    /**
     * Attempts to remove an Attribute by name
     * 
     * @param name Attribute Name
     * @throws IllegalArgumentException Attribute not found
     */
    public void removeAttribute(String name) {
        for (Attribute attr : _attributes) {
            // Found stored Attribute
            if (attr.getName().equals(name)) {
                if (attr.isPrimaryKey()) {
                    _relationships.clear();
                    EntityManager.entityClearedRelationships(this);
                }

                _attributes.remove(attr);

                return;
            }
        }

        // No Attribute with name found
        throw new IllegalArgumentException("Attribute not found");
    }

    /**
     * Gets the attributes.
     * 
     * @return The Entity's attributes
     */
    public List<Attribute> getAttributes() {
        return Collections.unmodifiableList(_attributes);
    }

    /**
     * Method for adding relationships to an Entity
     * 
     * @param name The relationship name. Follows same naming convention as an Entity.
     * @param entity The other Entity
     * @throws IllegalArgumentException No Primary Key, Invalid Relationship Name specified, or EntityManager doesn't know about Entity
     */
    public void addRelationship(String name, Entity entity) throws IllegalArgumentException {
        if (getPrimaryKey() == null) {
            throw new IllegalArgumentException("Must have a primary key Attribute to add Relationships");
        }

        EntityManager.validateName(name, "Relationship");

        if (entity == null || !EntityManager.containsEntity(entity)) {
            throw new IllegalArgumentException("Invalid Entity provided");
        }

        _relationships.add(new Relationship(name, entity));
    }

    /**
     * Attempts to get and set a Relationship's name
     * 
     * @param oldName The desired Relationship's name
     * @param newName The new name for that attribute
     * @throws IllegalArgumentException Invalid Relationship Name specified, or Relationship not found
     */
    public void updateRelationshipName(String oldName, String newName) {
        Relationship storedRelationship = null;

        for (Relationship relationship : _relationships) {
            // Found stored attribute
            if (relationship.getName().equals(oldName)) {
                storedRelationship = relationship;
            }

            // Relationship name is in use
            if (relationship.getName().equals(newName)) {
                throw new IllegalArgumentException("Relationship name in use");
            }
        }

        // No relationship with old name found
        if (storedRelationship == null) {
            throw new IllegalArgumentException("Relationship not found");
        }

        storedRelationship.setName(newName);
    }

    /**
     * Attempts to get and set an Relationship's name
     * 
     * @param oldName The desired Relationship's name
     * @param otherEntity The other Entity
     * @throws IllegalArgumentException Relationship not found, or invalid Entity
     */
    public void updateRelationshipEntity(String oldName, Entity otherEntity) {
        for (Relationship relationship : _relationships) {
            // Found stored attribute
            if (relationship.getName().equals(oldName)) {
                if (!EntityManager.containsEntity(otherEntity)) {
                    throw new IllegalArgumentException("Invalid Entity provided");
                }

                relationship.setEntity(otherEntity);
                return;
            }
        }

        // No relationship with old name found
        throw new IllegalArgumentException("Relationship not found");
    }

    /**
     * Attempts to remove a Relationship by name
     * 
     * @param name RelationshipName
     * @throws IllegalArgumentException Relationship not found
     */
    public void removeRelationship(String name) {
        for (Relationship rltn : _relationships) {
            // Found stored Relationship
            if (rltn.getName().equals(name)) {
                _relationships.remove(rltn);
                return;
            }
        }

        // No Relationship with name found
        throw new IllegalArgumentException("Relationship not found");
    }

    /**
     * Gets the relationships.
     * 
     * @return The Entity's relationships
     */
    public List<Relationship> getRelationships() {
        return Collections.unmodifiableList(_relationships);
    }

    /**
     * @return This Entity in string form
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(_name).append('$');

        for (Attribute attribute : _attributes) {
            sb.append(attribute.getName())
              .append(':')
              .append(attribute.getType().name())
              .append(attribute.isPrimaryKey() ? "_PRIMARY_KEY" : "")
              .append('#');
        }

        sb.append("$");
        for (Relationship relationship : _relationships) {
            sb.append(relationship.getName())
              .append(':')
              .append(relationship.getEntity().getName())
              .append('#');
        }

        return sb.toString();
    }

    /**
     * Generates an Entity from a formatted string
     * 
     * @param entityText The formatted Entity string Should be of form NAME$ATTR1NAME:TYPE#ATTR2NAME:TYPE#
     * @return A new Entity
     * @throws IllegalArgumentException Improperly named Entity, Attributes, Relationships, or poorly formatted string
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

            boolean isPrimaryKey = attrPieces[1].contains("_PRIMARY_KEY");
            if (isPrimaryKey) {
                attrPieces[1] = attrPieces[1].replace("_PRIMARY_KEY", "");
            }

            retEnt.addAttribute(attrPieces[0], AttributeType.valueOf(attrPieces[1]));
            retEnt.setPrimaryKey(attrPieces[0], isPrimaryKey);
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

            String otherEntityName = relPieces[1];
            Entity otherEntity = EntityManager.getEntityByName(otherEntityName);

            if (otherEntity == null) {
                throw new IllegalArgumentException("Other Entity, \"" + otherEntityName + ",\" doesn't exist");
            }

            retEnt.addRelationship(relPieces[0], otherEntity);
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
