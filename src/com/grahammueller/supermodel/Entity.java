package com.grahammueller.supermodel;

import java.util.HashMap;

public class Entity {
    public enum Type { INTEGER_PRIMARY_KEY, NUMERIC, TEXT, BLOB }

    private String _name;
    private HashMap<String, Type>_attributes;

    /**
     * Default constructor for an Entity
     *
     * @param name Name of the Entity. Should not contain characters, or start with a number.
     * @throws IllegalArgumentException Invalid Entity Name specified
     */
    public Entity(String name) throws IllegalArgumentException {
        if (name.isEmpty())
            throw new IllegalArgumentException("Entity name not specified");

        if (name.matches(".*\\W.*"))
            throw new IllegalArgumentException("Invalid characters in name");

        if (name.matches("\\d.*"))
            throw new IllegalArgumentException("Entity name can't start with a number");

        _name = name;
        _attributes = new HashMap<String, Type>();
    }

    /**
     * Method for adding attributes to an entity
     *
     * @param name The attribute name. Follows same naming convention as an Entity.
     * @param type The type of the attribute. Can be any of the Entity.Type Enum values.
     * @return Whether the attribute was added
     */
    public boolean addAttribute(String name, Type type) {
        // TODO validate attribute

        _attributes.put(name, type);

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
     * Generates an Entity from a formatted string
     * @param entityText The formatted Entity string
     * Should be of form NAME$ATTR1NAME:TYPE#ATTR2NAME:TYPE#
     * @return A new Entity
     * @throws IllegalArgumentException Improperly named Entity, Attributes, or poorly formatted string
     */
    public static Entity fromString(String entityText) throws IllegalArgumentException{
        Entity retEnt = null;

        int nameBreak = entityText.indexOf('$');

        if (nameBreak < 0) throw new IllegalArgumentException("Name not properly specified");

        retEnt = new Entity(entityText.substring(0, nameBreak));
        String[] attributes = entityText.substring(nameBreak + 1).split("#");

        for (String attribute : attributes) {
            String attrName = attribute.substring(0, attribute.indexOf(":"));
            String attrType = attribute.substring(attribute.indexOf(":") + 1);

            retEnt.addAttribute(attrName, Type.valueOf(attrType));
        }

        return retEnt;
    }
}
