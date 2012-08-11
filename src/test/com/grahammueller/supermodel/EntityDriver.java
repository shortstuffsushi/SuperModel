package test.com.grahammueller.supermodel;

import static org.junit.Assert.*;

import org.junit.*;

import com.grahammueller.supermodel.Entity;

public class EntityDriver {
    @Test
    public void testStandardEntityCreation() {
        Entity entity = Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#");

        assertEquals(4, entity.attributes().size());

        assertTrue(entity.attributes().containsKey("id"));
        assertTrue(entity.attributes().containsKey("name"));
        assertTrue(entity.attributes().containsKey("type"));
        assertTrue(entity.attributes().containsKey("image"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEntityMissingName() {
        Entity.fromString("id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEntityNameTooShort() {
        Entity.fromString("$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalCharactersInEntityName() {
        Entity.fromString("P*kemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEntityNameStartsWithANumber() {
        Entity.fromString("315Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#");
    }
}
