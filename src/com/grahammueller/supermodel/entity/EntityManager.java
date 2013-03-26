package com.grahammueller.supermodel.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * The manager of Entity interaction.
 * Validates interaction with Entity objects.
 * 
 * TODO It would be nicer if this acted more as a Factory,
 *      Entities were created and updated by interacting
 *      with the manager, rather than the object.
 */
public class EntityManager {
    /**
     * Register Entity to the list of registered Entity if one with the same name doesn't exist
     * 
     * @param Entity the new Entity
     * @throws IllegalArgumentException Entity already exists
     */
    protected static void registerEntity(Entity e) {
        // Don't add if we've already got it
        if (containsEntity(e)) {
            throw new IllegalArgumentException("Entity already registered");
        }

        _entities.add(e);

        for (EntityManagerListener listener : _listeners) {
            listener.entityAdded(e);
        }
    }

    /**
     * Attempts to update Entity name, if it is valid and no Entity already exists with the same name
     * 
     * @param name The new name
     * @throws IllegalArgumentException Invalid Entity Name specified, or name already in use
     */
    protected static void updateEntityName(Entity e, String newName) {
        if (containsEntity(newName)) {
            throw new IllegalArgumentException("Entity name already in use");
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
    }

    /**
     * Determines if an Entity is already registered
     * 
     * @param Name the Name to look for
     * @return Whether it is registered
     */
    public static boolean containsEntity(String name) {
        return getEntityByName(name) != null;
    }

    /**
     * Determines if an Entity is already registered
     * 
     * @param Name the Name to look for
     * @return Whether it is registered
     */
    public static boolean containsEntity(Entity e) {
        return _entities.contains(e);
    }

    /**
     * Validates a name
     * 
     * @param name The name to check
     * @param caller The part of the entity checking for validity
     * @throws IllegalArgumentException For invalid names
     */
    protected static void validateName(String name, String caller) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) { throw new IllegalArgumentException(caller + " name not specified"); }

        if (name.matches(".*\\W.*")) { throw new IllegalArgumentException(String.format("Invalid characters in %s name", caller)); }

        if (name.matches("\\d.*")) { throw new IllegalArgumentException(caller + " name can't start with a number"); }
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

    /**
     * Gets all entities.
     * TODO read-only or copy
     * @return All the Entity objects
     */
    public static List<Entity> getAllEntities() {
        return _entities;
    }

    /**
     * Tries to get specified Entity.
     * @return Entity requested, or null.
     */
    public static Entity getEntityByName(String entityName) {
        for (Entity entity : _entities) {
            if (entity.getName().equals(entityName)) {
                return entity;
            }
        }

        return null;
    }

    private static ArrayList<Entity> _entities = new ArrayList<Entity>();
    private static ArrayList<EntityManagerListener> _listeners = new ArrayList<EntityManagerListener>();
}
