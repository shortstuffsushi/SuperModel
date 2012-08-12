package test.com.grahammueller.supermodel;

import static org.junit.Assert.*;

import org.junit.*;

import com.grahammueller.supermodel.Entity;

public class EntityDriver {

    @Test
    public void testStandardEntityCreation() {
        Entity entity = Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");

        assertEquals(4, entity.attributes().size());

        assertTrue(entity.attributes().containsKey("id"));
        assertTrue(entity.attributes().containsKey("name"));
        assertTrue(entity.attributes().containsKey("type"));
        assertTrue(entity.attributes().containsKey("image"));

        assertEquals(1, entity.relationships().size());
    }

    @Test
    public void testEmptyEntity() {
        Entity entity = Entity.fromString("Pokemon$$");

        assertEquals(0, entity.attributes().size());
        assertEquals(0, entity.relationships().size());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testMalformedEntityString() {
        Entity.fromString("Pokemon$");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEntityMissingName() {
        Entity.fromString("id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEntityNameTooShort() {
        Entity.fromString("$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalCharactersInEntityName() {
        Entity.fromString("P*kemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEntityNameStartsWithANumber() {
        Entity.fromString("315Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAttributeMissingName() {
        Entity.fromString("Pokemon$INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAttributeNameTooShort() {
        Entity.fromString("Pokemon$:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalCharactersInAttributeName() {
        Entity.fromString("Pokemon$i*d:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAttributeNameStartsWithANumber() {
        Entity.fromString("Pokemon$315id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRelationshipMissingName() {
        Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRelationshipNameTooShort() {
        Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalCharactersInRelationshipName() {
        Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$ow*ner:TRAINER#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testRelationshipNameStartsWithANumber() {
        Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$315owner:TRAINER#");
    }
}
