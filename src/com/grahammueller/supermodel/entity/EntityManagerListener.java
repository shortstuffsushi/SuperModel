package com.grahammueller.supermodel.entity;

import java.util.Map;

/**
 * Interface for classes that need to be informed
 * about updates to the managed list of Entities.
 */
public interface EntityManagerListener {
    /**
     * This method will be called any time a new Entity is added to the Entity Managed
     * 
     * @param e The entity being added
     */
    public void entityAdded(Entity e);

    /**
     * This method will be called any time an Entity is updated to in Entity Managed
     * 
     * @param e The entity being added
     * @param updateInfo Map of update information of form
     *        {
     *            "name" : "updated-attribute-name-string",
     *            "new"  : <new-value-object>,
     *            "old"  : <old-value-object> (where relevant) 
     *        }
     */
    public void entityUpdated(Entity e, Map<String, Object> updateInfo);

    /**
     * This method will be called any time an Entity is removed from the Entity Manager
     * 
     * @param e The entity being removed
     */
    public void entityRemoved(Entity e);
}
