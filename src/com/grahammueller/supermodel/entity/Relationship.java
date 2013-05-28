package com.grahammueller.supermodel.entity;

/**
 * A Relationship between Entities
 */
public class Relationship {
    /**
     * Creates a Relationship for an Entity.
     * 
     * TODO Make this class immutable
     * 
     * @param name The name for the Relationship
     * @param entity The other Entity
     */
    public Relationship(String name, Entity entity) {
      _name = name;
      _entity = entity;
    }

    /**
     * Gets the Relationship name
     * @return The name
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets the Relationship name
     * @param name The new name
     */
    public void setName(String name) {
        _name = name; 
    }

    /**
     * Gets the other Entity type
     * @return Other Entity type
     */
    public Entity getEntity() {
        return _entity;
    }

    /**
     * Sets the other Entity
     * @param value Other Entity
     */
    public void setEntity(Entity entity) {
        _entity = entity;
    }

    private String _name;
    private Entity _entity;
}
