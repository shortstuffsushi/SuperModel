package test.com.grahammueller.supermodel;

import static org.junit.Assert.*;

import org.junit.*;

import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;

public class EntityManagerDriver {

    @Before
    public void setUp() {
        EntityManager.clearRegistry();
    }

    @Test
    public void testBasicEntityRegistry() {
        Entity entity = Entity.fromString("Pokemon$id:INTEGER#name:STRING#type:FLOAT#image:BLOB#$#");

        assertTrue(EntityManager.containsEntity(entity));
    }

    @Test
    public void testDuplicateNamesNotAllowed() {
        Entity.fromString("Pokemon$id:INTEGER#name:STRING#type:INTEGER#image:BLOB#$#");

        String failureMessage = null;
        try {
            Entity.fromString("Pokemon$id:INTEGER#name:STRING#type:INTEGER#image:BLOB#$#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Entity already registered", failureMessage);
    }

    @Test
    public void testContainsWithString() {
        Entity trainer = Entity.fromString("Trainer$id:INTEGER#name:STRING#$#");
        Entity pokemon = Entity.fromString("Pokemon$id:INTEGER#name:STRING#type:FLOAT#image:BLOB#$owner:Trainer#");

        assertTrue(EntityManager.containsEntity(trainer.getName()));
        assertTrue(EntityManager.containsEntity(pokemon.getName()));
    }

    @Test
    public void testContainsWithStringGettingNonExistantEntity() {
        assertFalse(EntityManager.containsEntity("Something"));
    }

    @Test
    public void testBasicRemoveEntity() {
        Entity trainer = Entity.fromString("Trainer$id:INTEGER#name:STRING#$#");

        EntityManager.removeEntity(trainer);

        assertEquals(0, EntityManager.getAllEntities().size());
    }

    @Test
    public void testBasicRemoveEntityThatRelationshipsPointTo() {
        Entity trainer = Entity.fromString("Trainer$id:INTEGER#name:STRING#$#");
        Entity pokemon = Entity.fromString("Pokemon$id:INTEGER#name:STRING#type:FLOAT#image:BLOB#$owner:Trainer#");

        EntityManager.removeEntity(trainer);

        assertEquals(1, EntityManager.getAllEntities().size());
        assertEquals(0, pokemon.getRelationships().size());
    }
}
