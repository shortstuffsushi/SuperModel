package test.com.grahammueller.supermodel;

import static org.junit.Assert.*;

import org.junit.*;

import com.grahammueller.supermodel.Entity;
import com.grahammueller.supermodel.EntityManager;

public class EntityDriver {
    private String failureMessage;

    @Before
    public void setUp() {
        EntityManager.clearRegistry();
        failureMessage = null;
    }

    @Test
    public void testStandardEntityCreation() {
        Entity entity = Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");

        assertEquals(4, entity.getAttributes().size());

        assertTrue(entity.getAttributes().containsKey("id"));
        assertTrue(entity.getAttributes().containsKey("name"));
        assertTrue(entity.getAttributes().containsKey("type"));
        assertTrue(entity.getAttributes().containsKey("image"));

        assertEquals(1, entity.getRelationships().size());
    }

    @Test
    public void testEmptyEntity() {
        Entity entity = Entity.fromString("Pokemon$$");

        assertEquals(0, entity.getAttributes().size());
        assertEquals(0, entity.getRelationships().size());
    }

    @Test
    public void testMalformedEntityString() {
        try {
            Entity.fromString("Pokemon$");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Attributes and relationships not properly specified", failureMessage);
    }

    @Test
    public void testEntityMissingName() {
        try {
            Entity.fromString("$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Entity name not specified", failureMessage);
    }

    @Test
    public void testIllegalCharactersInEntityName() {
        try {
            Entity.fromString("P*kemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }
    
        assertEquals("Invalid characters in Entity name", failureMessage);
    }

    @Test
    public void testEntityNameStartsWithANumber() {
        try {
            Entity.fromString("315Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Entity name can't start with a number", failureMessage);
    }

    @Test
    public void testAttributeMissingName() {
        try {
            Entity.fromString("Pokemon$INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Attribute name not specified", failureMessage);
    }

    @Test
    public void testIllegalCharactersInAttributeName() {
        try {
            Entity.fromString("Pokemon$i*d:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Invalid characters in Attribute name", failureMessage);
    }

    @Test
    public void testAttributeNameStartsWithANumber() {
        try {
            Entity.fromString("Pokemon$315id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Attribute name can't start with a number", failureMessage);
    }

    @Test
    public void testRelationshipMissingName() {
        try {
            Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship name not specified", failureMessage);
    }

    @Test
    public void testRelationshipNameTooShort() {
        try {
            Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship name not specified", failureMessage);
    }

    @Test
    public void testIllegalCharactersInRelationshipName() {
        try {
            Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$ow*ner:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Invalid characters in Relationship name", failureMessage);
    }

    @Test
    public void testRelationshipNameStartsWithANumber() {
        try {
            Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$315owner:TRAINER#");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship name can't start with a number", failureMessage);
    }
}
