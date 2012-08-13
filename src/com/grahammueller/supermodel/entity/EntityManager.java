package com.grahammueller.supermodel.entity;

import java.util.ArrayList;

public class EntityManager {
    private static ArrayList<Entity>entities = new ArrayList<Entity>();

    /*
     * Register Entity to the list of registered Entity
     * if one with the same name doesn't exist
     * @param Entity the new Entity
     * @return Whether the entity has been added
     */
    public static boolean registerEntity(Entity e) {
        // Don't add if we've already got it
        if (containsEntity(e)) return false;

        entities.add(e);
        return true;
    }

    /*
     * Determines if an Entity is registered
     * @param Entity the Entity to look for
     * @return Whether it is registered
     */
    public static boolean containsEntity(Entity e) {
        return entities.contains(e);
    }

    /*
     * Removes all registered Entities
     */
    public static void clearRegistry() {
        entities.clear();
    }
}
