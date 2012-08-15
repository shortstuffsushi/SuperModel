package com.grahammueller.supermodel.entity;

import java.util.HashMap;
import java.util.Map.Entry;

public class Entity {
    public enum Type { INTEGER_PRIMARY_KEY, NUMERIC, TEXT, BLOB }

    private String _name;
    private HashMap<String, Type>_attributes;
    private HashMap<String, String>_relationships;

    /**
     * Default constructor for an Entity
     *
     * @param name Name of the Entity. Should not contain characters, or start with a number.
     * @throws IllegalArgumentException Invalid Entity Name specified
     */
    public Entity(String name) throws IllegalArgumentException {
        validateName(name, "Entity");

        _name = name;
        _attributes = new HashMap<String, Type>();
        _relationships = new HashMap<String, String>();

        // Attempt to register, throw exception on failure
        // which indicates another entity with this name exists
        if(!EntityManager.registerEntity(this)) throw new IllegalArgumentException("Unable to register Entity");
    }

    /**
     * Method for adding attributes to an Entity
     *
     * @param name The attribute name. Follows same naming convention as an Entity.
     * @param type The type of the attribute. Can be any of the Entity.Type Enum values.
     * @return Whether the attribute was added
     */
    public boolean addAttribute(String name, Type type) {
        validateName(name, "Attribute");

        _attributes.put(name, type);

        return true;
    }

    /**
     * Method for adding relationships to an Entity
     *
     * @param name The relationship name. Follows same naming convention as an Entity.
     * @param type The Entity name of the linked Entity. Can be anything, but when validation
     *             is run, it will check to see that an Entity with the name exists
     * @return Whether the attribute was added
     */
    public boolean addRelationship(String name, String entity) {
        validateName(name, "Relationship");

        _relationships.put(name, entity);

        return true;
    }

    /**
     * Gets the name of thie Entity.
     * @return The Entity's name
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets the attributes.
     * TODO this should probably be returning a clone/readonly version
     * @return The Entity's attributes
     */
    public HashMap<String, Type> getAttributes() {
        return _attributes;
    }

    /**
     * Gets the relationships.
     * TODO this should probably be returning a clone/readonly version
     * @return The Entity's relationships
     */
    public HashMap<String, String> getRelationships() {
        return _relationships;
    }

    /**
     * Validates a name
     * @param name The name to check
     * @param caller The part of the entity checking for validity
     * @throws IllegalArgumentException For invalid names
     */
    private void validateName(String name, String caller) throws IllegalArgumentException {
        if (name.isEmpty())
            throw new IllegalArgumentException(caller + " name not specified");

        if (name.matches(".*\\W.*"))
            throw new IllegalArgumentException(String.format("Invalid characters in %s name", caller));

        if (name.matches("\\d.*"))
            throw new IllegalArgumentException(caller + " name can't start with a number");
    }

    /**
     * @return This Entity in string form
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(_name).append('$');

        for (Entry<String, Type> attribute : _attributes.entrySet()) {
            sb.append(attribute.getKey()).append(':').append(attribute.getValue().toString()).append('#');
        }

        sb.append("$");
        for (Entry<String, String> relationship : _relationships.entrySet()) {
            sb.append(relationship.getKey()).append(':').append(relationship.getValue()).append('#');
        }

        return sb.toString();
    }

    /**
     * Generates an Entity from a formatted string
     * @param entityText The formatted Entity string
     * Should be of form NAME$ATTR1NAME:TYPE#ATTR2NAME:TYPE#
     * @return A new Entity
     * @throws IllegalArgumentException Improperly named Entity, Attributes, or poorly formatted string
     */
    public static Entity fromString(String entityText) throws IllegalArgumentException{
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
            if (attribute.isEmpty()) continue;

            String[] attrPieces = attribute.split(":");

            if (attrPieces.length != 2) throw new IllegalArgumentException("Attribute malformed");

            retEnt.addAttribute(attrPieces[0], Type.valueOf(attrPieces[1]));
        }

        for (String relationship : relationships) {
            // Remove leading and trailing spaces
            relationship = relationship.trim();

            // Skip empty relationships
            if (relationship.isEmpty()) continue;

            String[] relPieces = relationship.split(":");

            // Report malformed attributes
            if (relPieces.length != 2) throw new IllegalArgumentException("Relationship malformed");

            retEnt.addRelationship(relPieces[0], relPieces[1]);
        }

        return retEnt;
    }

    /**
     * Determines equality based on name.
     * @param Object other object
     * @return Whether objects are equal
     */
    public boolean equals(Object o) {
        return (o instanceof Entity) && _name.equals(((Entity) o).getName());
    }
}
