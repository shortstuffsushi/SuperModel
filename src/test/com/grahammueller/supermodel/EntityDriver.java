package test.com.grahammueller.supermodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.grahammueller.supermodel.entity.Attribute;
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;

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

        List<Attribute> attributes = entity.getAttributes();
        assertEquals(4, attributes.size());

        assertTrue(attributes.get(0).getName().equals("id"));
        assertTrue(attributes.get(1).getName().equals("name"));
        assertTrue(attributes.get(2).getName().equals("type"));
        assertTrue(attributes.get(3).getName().equals("image"));

        assertEquals(1, entity.getRelationships().size());
    }

    @Test
    public void testAllowEmptyEntity() {
        Entity entity = Entity.fromString("Pokemon$$");

        assertEquals(0, entity.getAttributes().size());
        assertEquals(0, entity.getRelationships().size());
    }

    @Test
    public void testAllowEmptyRelationships() {
        Entity entity = Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#$");

        assertEquals(1, entity.getAttributes().size());
        assertEquals(0, entity.getRelationships().size());
    }

    @Test
    public void testAllowEmptyAttributes() {
        Entity entity = Entity.fromString("Pokemon$$owner:TRAINER#");

        assertEquals(0, entity.getAttributes().size());
        assertEquals(1, entity.getRelationships().size());
    }

    @Test
    public void testMalformedEntityString() {
        try {
            Entity.fromString("Pokemon$");
        }
        catch(IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Entity malformed", failureMessage);
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

        assertEquals("Attribute malformed", failureMessage);
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

        assertEquals("Relationship malformed", failureMessage);
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
