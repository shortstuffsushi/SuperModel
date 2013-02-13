package com.grahammueller.supermodel.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class EntityManager {
    /**
     * Register Entity to the list of registered Entity if one with the same name doesn't exist
     * 
     * @param Entity the new Entity
     * @return Whether the entity has been added
     */
    protected static boolean registerEntity(Entity e) {
        // Don't add if we've already got it
        if (containsEntity(e)) { return false; }

        _entities.add(e);

        for (EntityManagerListener listener : _listeners) {
            listener.entityAdded(e);
        }

        return true;
    }

    /**
     * Attempts to update Entity name, if it is valid and no Entity already exists with the same name
     * 
     * @param name The new name
     * @throws IllegalArgumentException Invalid Entity Name specified
     * @return Whether set was successful
     */
    protected static boolean updateEntityName(Entity e, String newName) {
        if (containsName(newName)) {
            return false;
        }

        validateName(newName, "Entity");

        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("name", "name");
        updates.put("old", e._name);
        updates.put("new", newName);

        e._name = newName;

        for (EntityManagerListener listener : _listeners) {
            listener.entityUpdated(e, updates);
        }

        return true;
    }

    /**
     * Determines if an Entity Name is registered
     * 
     * @param Name the Name to look for
     * @return Whether it is registered
     */
    protected static boolean containsName(String name) {
        for (Entity e : _entities) {
            if (e.getName().equals(name)) { return true; }
        }

        return false;
    }

    /**
     * Validates a name
     * 
     * @param name The name to check
     * @param caller The part of the entity checking for validity
     * @throws IllegalArgumentException For invalid names
     */
    protected static void validateName(String name, String caller) throws IllegalArgumentException {
        if (name.isEmpty()) { throw new IllegalArgumentException(caller + " name not specified"); }

        if (name.matches(".*\\W.*")) { throw new IllegalArgumentException(String.format("Invalid characters in %s name", caller)); }

        if (name.matches("\\d.*")) { throw new IllegalArgumentException(caller + " name can't start with a number"); }
    }

    /**
     * Determines if an Entity is registered
     * 
     * @param Entity the Entity to look for
     * @return Whether it is registered
     */
    public static boolean containsEntity(Entity e) {
        return _entities.contains(e);
    }

    /**
     * Removes all registered Entities
     */
    public static void clearRegistry() {
        _entities.clear();
    }

    /**
     * Iterates over and gathers all the managed Entity names
     *
     * @return A list of strings with all the Entity names
     */
    public static List<String> nameList() {
        ArrayList<String> names = new ArrayList<String>();

        for (Entity e : _entities) {
            names.add(e.getName());
        }

        return names;
    }

    /**
     * Adds the passed listener to the list of listeners for Entity updates
     * @param eml The EntityManagerListener to be added
     */
    public static void registerForEntityUpdates(EntityManagerListener eml) {
        _listeners.add(eml);
    }

    private static ArrayList<Entity> _entities = new ArrayList<Entity>();
    private static ArrayList<EntityManagerListener> _listeners = new ArrayList<EntityManagerListener>();
}
