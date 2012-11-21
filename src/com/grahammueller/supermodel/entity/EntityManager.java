package com.grahammueller.supermodel.entity;

import java.util.ArrayList;

public class EntityManager {
    private static ArrayList<Entity>entities = new ArrayList<Entity>();

    /**
     * Register Entity to the list of registered Entity
     * if one with the same name doesn't exist
     * @param Entity the new Entity
     * @return Whether the entity has been added
     */
    protected static boolean registerEntity(Entity e) {
        // Don't add if we've already got it
        if (containsEntity(e)) return false;

        entities.add(e);
        return true;
    }

    /**
     * Determines if an Entity is registered
     * @param Entity the Entity to look for
     * @return Whether it is registered
     */
    protected static boolean containsEntity(Entity e) {
        return entities.contains(e);
    }
    
    /**
     * Determines if an Entity Name is registered
     * @param Name the Name to look for
     * @return Whether it is registered
     */
    protected static boolean containsName(String name) {
    	for (Entity e : entities) {
    		if (e.getName().equals(name)) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    /**
     * Validates a name
     * @param name The name to check
     * @param caller The part of the entity checking for validity
     * @throws IllegalArgumentException For invalid names
     */
    protected static void validateName(String name, String caller) throws IllegalArgumentException {
        if (name.isEmpty())
            throw new IllegalArgumentException(caller + " name not specified");

        if (name.matches(".*\\W.*"))
            throw new IllegalArgumentException(String.format("Invalid characters in %s name", caller));

        if (name.matches("\\d.*"))
            throw new IllegalArgumentException(caller + " name can't start with a number");
    }

    /**
     * Removes all registered Entities
     */
    protected static void clearRegistry() {
        entities.clear();
    }
}
