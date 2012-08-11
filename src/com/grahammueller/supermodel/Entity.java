package com.grahammueller.supermodel;

import java.util.HashMap;

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
     * Gets the attributes.
     * TODO this should probably be returning a clone/readonly version
     * @return The Entity's attributes
     */
    public HashMap<String, Type> attributes() {
        return _attributes;
    }

    /**
     * Gets the relationships.
     * TODO this should probably be returning a clone/readonly version
     * @return The Entity's relationships
     */
    public HashMap<String, String> relationships() {
        return _relationships;
    }

    /**
     * @return This Entity in string form
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(_name).append('$');

        for (String attribute : _attributes.keySet()) {
            sb.append(attribute).append(':').append(_attributes.get(attribute).toString()).append('#');
        }

        return sb.toString();
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
            throw new IllegalArgumentException(caller + "name can't start with a number");
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

        int nameBreak = entityText.indexOf('$');
        if (nameBreak < 0) throw new IllegalArgumentException("Entity name not properly specified");

        int attrBreak = entityText.indexOf('$', nameBreak + 1);
        if (attrBreak < 0) throw new IllegalArgumentException("Attributes and relationships not properly specified");

        retEnt = new Entity(entityText.substring(0, nameBreak));
        String[] attributes = entityText.substring(nameBreak + 1, attrBreak).split("#");
        String[] relationships = entityText.substring(attrBreak + 1).split("#");

        for (String attribute : attributes) {
            attrBreak = attribute.indexOf(":");
            if (attrBreak < 0) throw new IllegalArgumentException("Relationship name not properly specified");
           String attrName = attribute.substring(0, attrBreak);
            String attrType = attribute.substring(attrBreak + 1);

            retEnt.addAttribute(attrName, Type.valueOf(attrType));
        }

        for (String relationship : relationships) {
            int relBreak = relationship.indexOf(":");
            if (relBreak < 0) throw new IllegalArgumentException("Relationship name not properly specified");
            String relName = relationship.substring(0, relBreak);
            String relEnt = relationship.substring(relBreak + 1);

            retEnt.addRelationship(relName, relEnt);
        }

        return retEnt;
    }
}
