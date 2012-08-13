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
        Entity entity = Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");

        assertTrue(EntityManager.containsEntity(entity));
    }

    @Test
    public void testDuplicateNamesNotAllowed() {
        Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");

        String failureMessage = null;
        try {
            Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Unable to register Entity", failureMessage);
    }
}
