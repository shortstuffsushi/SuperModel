package test.com.grahammueller.supermodel;

import static org.junit.Assert.*;
import java.util.Map;

import org.junit.*;

import com.grahammueller.supermodel.entity.AttributeType;
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;
import com.grahammueller.supermodel.entity.EntityManagerListener;

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
        Entity pokemon = Entity.fromString("Pokemon$id:INTEGER#name:STRING#type:FLOAT#image:BLOB#$");
        Entity trainer = Entity.fromString("Trainer$id:INTEGER_PRIMARY_KEY#name:STRING#$party:Pokemon#");

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
        Entity pokemon = Entity.fromString("Pokemon$id:INTEGER#name:STRING#type:FLOAT#image:BLOB#$");
        Entity trainer = Entity.fromString("Trainer$id:INTEGER_PRIMARY_KEY#name:STRING#$party:Pokemon#");

        EntityManager.removeEntity(trainer);

        assertEquals(1, EntityManager.getAllEntities().size());
        assertEquals(0, pokemon.getRelationships().size());
    }

    @Test
    public void testListenerMethodForAdd() {
        EntityManagerListenerImpl addTester = new EntityManagerListenerImpl();
        EntityManager.registerForEntityUpdates(addTester);

        Entity e = new Entity("Pokemon");
        assertTrue(addTester.didHitAdded);
        assertEquals(e, addTester.affectedEntity);

        EntityManager.unregisterForEntityUpdates(addTester);
    }

    @Test
    public void testListenerMethodForUpdated() {
        EntityManagerListenerImpl updateTester = new EntityManagerListenerImpl();
        EntityManager.registerForEntityUpdates(updateTester);

        Entity e = new Entity("Pokemon");
        e.setName("SomethingElse");

        assertTrue(updateTester.didHitUpdated);
        assertEquals(e, updateTester.affectedEntity);

        Map<String, Object> affectedInfo = updateTester.affectedInfo;
        assertEquals(affectedInfo.get("name"), "name");
        assertEquals(affectedInfo.get("old"), "Pokemon");
        assertEquals(affectedInfo.get("new"), "SomethingElse");

        EntityManager.unregisterForEntityUpdates(updateTester);
    }

    @Test
    public void testListenerMethodForRelationshipsRemoved() {
        EntityManagerListenerImpl updateTester = new EntityManagerListenerImpl();
        EntityManager.registerForEntityUpdates(updateTester);

        Entity e = new Entity("Pokemon");
        e.addAttribute("id", AttributeType.INTEGER);
        e.setPrimaryKey("id", true);
        e.addRelationship("self", e);

        e.removeAttribute("id");
        assertEquals(0, e.getRelationships().size());

        assertTrue(updateTester.didHitUpdated);
        assertEquals(e, updateTester.affectedEntity);

        Map<String, Object> affectedInfo = updateTester.affectedInfo;
        assertEquals(affectedInfo.get("name"), "relationships-cleared");

        EntityManager.unregisterForEntityUpdates(updateTester);
    }

    @Test
    public void testListenerMethodForRemoved() {
        EntityManagerListenerImpl removeTester = new EntityManagerListenerImpl();
        EntityManager.registerForEntityUpdates(removeTester);

        Entity e = new Entity("Pokemon");
        EntityManager.removeEntity(e);

        assertTrue(removeTester.didHitRemoved);
        assertEquals(e, removeTester.affectedEntity);

        EntityManager.unregisterForEntityUpdates(removeTester);
    }

    private class EntityManagerListenerImpl implements EntityManagerListener {
        public boolean didHitAdded;
        public boolean didHitUpdated;
        public boolean didHitRemoved;
        public Entity affectedEntity;
        public Map<String, Object> affectedInfo;

        @Override
        public void entityAdded(Entity e) {
            didHitAdded = true;
            affectedEntity = e;
        }

        @Override
        public void entityUpdated(Entity e, Map<String, Object> updateInfo) {
            didHitUpdated = true;
            affectedEntity = e;
            affectedInfo = updateInfo;
        }

        @Override
        public void entityRemoved(Entity e) {
            didHitRemoved = true;
            affectedEntity = e;
        }
    }
}
