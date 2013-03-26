package test.com.grahammueller.supermodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.grahammueller.supermodel.entity.Attribute;
import com.grahammueller.supermodel.entity.AttributeType;
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;
import com.grahammueller.supermodel.entity.Relationship;

public class EntityDriver {
    private String failureMessage;

    @Before
    public void setUp() {
        EntityManager.clearRegistry();
        failureMessage = null;
    }
    
    @Test
    public void testStandardEntityCreation() {
        Entity entity = new Entity("TestEntity");

        assertNotNull(entity);
    }

    @Test
    public void testIllegalCharactersInEntityName() {
        try {
            new Entity("P*kemon");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Invalid characters in Entity name", failureMessage);
    }

    @Test
    public void testEntityNameStartsWithANumber() {
        try {
            new Entity("315Pokemon");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Entity name can't start with a number", failureMessage);
    }

    @Test
    public void testEntityNormalNameUpdate() {
        Entity e = new Entity("Pokemon");

        e.setName("Trainer");

        assertEquals("Trainer", e.getName());
    }

    @Test
    public void testEntityNormalNameInUse() {
        try {
            Entity trainer = new Entity("Trainer");
            Entity pkmn = new Entity("Pokemon");

            pkmn.setName(trainer.getName());
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Entity name already in use", failureMessage);
    }

    ///////////////
    // Attribute //
    ///////////////
    @Test
    public void testEntityAttributeMissingName() {
        try {
            Entity e = new Entity("Pokemon");

            e.addAttribute(null, AttributeType.BLOB);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Attribute name not specified", failureMessage);
    }

    @Test
    public void testEntityAttributeNameTooShort() {
        try {
            Entity e = new Entity("Pokemon");

            e.addAttribute("", AttributeType.BLOB);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Attribute name not specified", failureMessage);
    }

    @Test
    public void testEntityAttributeIllegalCharactersInName() {
        try {
            Entity e = new Entity("Pokemon");

            e.addAttribute("i*d", AttributeType.INTEGER_PRIMARY_KEY);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Invalid characters in Attribute name", failureMessage);
    }

    @Test
    public void testEntityAttributeNameStartsWithANumber() {
        try {
            Entity e = new Entity("Pokemon");

            e.addAttribute("315id", AttributeType.INTEGER_PRIMARY_KEY);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Attribute name can't start with a number", failureMessage);
    }

    @Test
    public void testEntityAttributeNormalNameUpdate() {
        Entity e = new Entity("Pokemon");

        e.addAttribute("id", AttributeType.INTEGER_PRIMARY_KEY);
        e.updateAttributeName("id", "pkmn_id");

        boolean containsId = false;
        boolean containsPkmnId = false;

        List<Attribute> attributes = e.getAttributes();
        for (Attribute attr : attributes) {
            if (attr.getName().equals("id")) {
                containsId = true;
            }
            else if (attr.getName().equals("pkmn_id")) {
                containsPkmnId = true;
            }
        }

        assertEquals(1, attributes.size());
        assertTrue(containsPkmnId);
        assertFalse(containsId);
    }

    @Test
    public void testEntityAttributeNotFoundNameUpdate() {
        Entity e = new Entity("Pokemon");

        e.addAttribute("id", AttributeType.INTEGER_PRIMARY_KEY);

        try {
            e.updateAttributeName("pkmn_id", "pkmnId");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Requested Attribute not found", failureMessage);
    }

    @Test
    public void testEntityAttributeNameInUseNameUpdate() {
        Entity e = new Entity("Pokemon");

        e.addAttribute("id", AttributeType.INTEGER_PRIMARY_KEY);
        e.addAttribute("pkmn_id", AttributeType.NUMERIC);

        try {
            e.updateAttributeName("id", "pkmn_id");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Attribute name in use", failureMessage);
    }

    @Test
    public void testEntityAttributeNormalTypeUpdate() {
        Entity e = new Entity("Pokemon");

        e.addAttribute("id", AttributeType.INTEGER_PRIMARY_KEY);
        e.updateAttributeType("id", AttributeType.NUMERIC);

        boolean containsIdAsPK = false;
        boolean containsIdAsNumeric = false;
        List<Attribute> attributes = e.getAttributes();
        for (Attribute attr : attributes) {
            if (attr.getName().equals("id")) {
                if (attr.getType() == AttributeType.INTEGER_PRIMARY_KEY) {
                    containsIdAsPK = true;
                }
                else if (attr.getType() == AttributeType.NUMERIC) {
                    containsIdAsNumeric = true;
                }
            }
        }

        assertEquals(1, attributes.size());
        assertTrue(containsIdAsNumeric);
        assertFalse(containsIdAsPK);
    }

    @Test
    public void testEntityAttributeNotFoundTypeUpdate() {
        Entity e = new Entity("Pokemon");

        e.addAttribute("id", AttributeType.INTEGER_PRIMARY_KEY);

        try {
            e.updateAttributeType("pkmn_id", AttributeType.NUMERIC);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Requested Attribute not found", failureMessage);
    }

    @Test
    public void testEntityAttributeNormalPKTypeUpdate() {
        Entity e = new Entity("Pokemon");

        e.addAttribute("id", AttributeType.NUMERIC);
        e.updateAttributeType("id", AttributeType.INTEGER_PRIMARY_KEY);

        boolean containsIdAsPK = false;
        boolean containsIdAsNumeric = false;
        List<Attribute> attributes = e.getAttributes();
        for (Attribute attr : attributes) {
            if (attr.getName().equals("id")) {
                if (attr.getType() == AttributeType.INTEGER_PRIMARY_KEY) {
                    containsIdAsPK = true;
                }
                else if (attr.getType() == AttributeType.NUMERIC) {
                    containsIdAsNumeric = true;
                }
            }
        }

        assertEquals(1, attributes.size());
        assertFalse(containsIdAsNumeric);
        assertTrue(containsIdAsPK);
    }

    @Test
    public void testEntityAttributeAlreadyHasPKTypeUpdate() {
        Entity e = new Entity("Pokemon");

        e.addAttribute("id", AttributeType.INTEGER_PRIMARY_KEY);
        e.addAttribute("pkmn_id", AttributeType.NUMERIC);

        try {
            e.updateAttributeType("pkmn_id", AttributeType.INTEGER_PRIMARY_KEY);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Already has primary key", failureMessage);
    }

    //////////////////
    // Relationship //
    //////////////////
    @Test
    public void testRelationshipMissingName() {
        try {
            Entity e = new Entity("Pokemon");

            e.addRelationship(null, e);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship name not specified", failureMessage);
    }

    @Test
    public void testRelationshipNameTooShort() {
        try {
            Entity e = new Entity("Pokemon");

            e.addRelationship("", e);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship name not specified", failureMessage);
    }

    @Test
    public void testIllegalCharactersInRelationshipName() {
        try {
            Entity e = new Entity("Pokemon");

            e.addRelationship("tw*n", e);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Invalid characters in Relationship name", failureMessage);
    }

    @Test
    public void testRelationshipNameStartsWithANumber() {
        try {
            Entity e = new Entity("Pokemon");

            e.addRelationship("315twin", e);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship name can't start with a number", failureMessage);
    }

    @Test
    public void testRelationshipWithNullEntity() {
        Entity e = new Entity("Pokemon");
        try {
            e.addRelationship("twin", null);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Invalid Entity provided", failureMessage);
    }

    @Test
    public void testRelationshipWithUnknownEntity() {
        Entity pkmn = new Entity("Pokemon");
        Entity trainer = new Entity("Trainer");

        // This is kind of unsafe... Just using for testing purposes
        EntityManager.clearRegistry();

        try {
            pkmn.addRelationship("owner", trainer);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Invalid Entity provided", failureMessage);
    }

    @Test
    public void testEntityRelationshipNormalNameUpdate() {
        Entity e = new Entity("Pokemon");

        e.addRelationship("self", e);
        e.updateRelationshipName("self", "twin");

        boolean containsSelf = false;
        boolean containsTwin = false;

        List<Relationship> relationships = e.getRelationships();
        for (Relationship rltn : relationships) {
            if (rltn.getName().equals("self")) {
                containsSelf = true;
            }
            else if (rltn.getName().equals("twin")) {
                containsTwin = true;
            }
        }

        assertEquals(1, relationships.size());
        assertTrue(containsTwin);
        assertFalse(containsSelf);
    }

    @Test
    public void testEntityRelationshipNotFoundNameUpdate() {
        Entity e = new Entity("Pokemon");

        e.addRelationship("self", e);

        try {
            e.updateRelationshipName("selfie", "twin");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship not found", failureMessage);
    }

    @Test
    public void testEntityRelationshipNameInUseNameUpdate() {
        Entity e = new Entity("Pokemon");

        e.addRelationship("self", e);
        e.addRelationship("twin", e);

        try {
            e.updateRelationshipName("self", "twin");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship name in use", failureMessage);
    }

    @Test
    public void testEntityRelationshipNormalEntityUpdate() {
        Entity pkmn = new Entity("Pokemon");
        Entity trainer = new Entity("Trainer");

        pkmn.addRelationship("owner", pkmn);
        pkmn.updateRelationshipEntity("owner", trainer);

        boolean containsOwnerAsPkmn = false;
        boolean containsOwnerAsTrainer = false;
        List<Relationship> relationships = pkmn.getRelationships();
        for (Relationship rltn : relationships) {
            if (rltn.getName().equals("owner")) {
                if (rltn.getEntity() == pkmn) {
                    containsOwnerAsPkmn = true;
                }
                else if (rltn.getEntity() == trainer) {
                    containsOwnerAsTrainer = true;
                }
            }
        }

        assertEquals(1, relationships.size());
        assertTrue(containsOwnerAsTrainer);
        assertFalse(containsOwnerAsPkmn);
    }

    @Test
    public void testEntityRelationshipNotFoundEntityUpdate() {
        Entity pkmn = new Entity("Pokemon");
        Entity trainer = new Entity("Trainer");

        pkmn.addRelationship("owner", pkmn);

        try {
            pkmn.updateRelationshipEntity("ownert", trainer);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship not found", failureMessage);
    }

    @Test
    public void testEntityRelationshipNullEntityUpdate() {
        Entity pkmn = new Entity("Pokemon");

        pkmn.addRelationship("owner", pkmn);

        try {
            pkmn.updateRelationshipEntity("owner", null);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Invalid Entity provided", failureMessage);
    }

    @Test
    public void testEntityRelationshipUnknownEntityUpdate() {
        Entity pkmn = new Entity("Pokemon");
        Entity trainer = new Entity("Trainer");

        pkmn.addRelationship("owner", pkmn);

        // This is kind of unsafe... Just using for testing purposes
        EntityManager.clearRegistry();

        try {
            pkmn.updateRelationshipEntity("owner", trainer);
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Invalid Entity provided", failureMessage);
    }

    ////////////////////
    // Lang Overrides //
    ////////////////////
    @Test
    public void testEntityToStringEmtpyEntity() {
        Entity e = new Entity("Pokemon");

        assertEquals("Pokemon$$", e.toString());
    }

    @Test
    public void testEntityToStringWithAttributes() {
        Entity e = new Entity("Pokemon");

        e.addAttribute("id", AttributeType.INTEGER_PRIMARY_KEY);
        e.addAttribute("name", AttributeType.TEXT);

        assertEquals("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#$", e.toString());
    }

    @Test
    public void testEntityToStringWithRelationships() {
        Entity pkmn = new Entity("Pokemon");
        Entity trainer = new Entity("Trainer");

        pkmn.addRelationship("owner", trainer);

        assertEquals("Pokemon$$owner:Trainer#", pkmn.toString());
    }

    @Test
    public void testEntityEqualsNormal() {
        Entity e1 = new Entity("Pokemon");
        Entity e2 = e1;

        // Not much of a test...
        assertTrue(e1.equals(e2));
    }

    @Test
    public void testEntityEqualsNull() {
        Entity e = new Entity("Pokemon");

        assertFalse(e.equals(null));
    }

    @Test
    public void testEntityEqualsDifferentClass() {
        Entity e = new Entity("Pokemon");

        assertFalse(e.equals(new Object()));
    }

    /////////////////
    // From String //
    /////////////////
    @Test
    public void testEntityFromStringStandardCreation() {
        Entity entity = Entity.fromString("Pokemon$$");

        assertNotNull(entity);
        assertEquals(0, entity.getAttributes().size());
        assertEquals(0, entity.getRelationships().size());
    }

    @Test
    public void testEntityFromStringWithAttributes() {
        Entity entity = Entity.fromString("Pokemon$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$");

        List<Attribute> attributes = entity.getAttributes();
        assertEquals(4, attributes.size());

        assertTrue(attributes.get(0).getName().equals("id"));
        assertTrue(attributes.get(1).getName().equals("name"));
        assertTrue(attributes.get(2).getName().equals("type"));
        assertTrue(attributes.get(3).getName().equals("image"));

        assertEquals(0, entity.getRelationships().size());
    }

    @Test
    public void testEntityFromStringWithInvalidAttributes() {
        try {
            Entity.fromString("Pokemon$id:INTEGER#$");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("No enum const class com.grahammueller.supermodel.entity.AttributeType.INTEGER", failureMessage);
    }

    @Test
    public void testEntityFromStringWithRelationships() {
        Entity trainer = new Entity("Trainer");
        Entity pkmn = Entity.fromString("Pokemon$$owner:Trainer#");

        assertNotNull(pkmn);

        List<Relationship> relationships = pkmn.getRelationships();

        assertEquals(1, relationships.size());

        boolean containsTrainer = false;
        boolean containsNameOwner = false;

        for (Relationship r : relationships) {
            if (r.getName().equals("owner")) {
                containsNameOwner = true;

                if (r.getEntity().equals(trainer)) {
                    containsTrainer = true;
                }
            }
        }

        assertTrue(containsTrainer);
        assertTrue(containsNameOwner);
    }

    @Test
    public void testEntityFromStringWithInvalidRelationships() {
        try {
            Entity.fromString("Pokemon$$owner:Trainer#");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Other Entity, \"Trainer,\" doesn't exist", failureMessage);
    }

    @Test
    public void testEntityFromStringMalformedString() {
        try {
            Entity.fromString("Pokemon$");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Entity malformed", failureMessage);
    }

    @Test
    public void testEntityFromStringMalformedAttribute() {
        try {
            Entity.fromString("Pokemon$id#$");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Attribute malformed", failureMessage);
    }

    @Test
    public void testEntityFromStringMalformedRelationship() {
        try {
            Entity.fromString("Pokemon$$id#");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Relationship malformed", failureMessage);
    }

    @Test
    public void testEntityFromStringMissingName() {
        try {
            Entity.fromString("$id:INTEGER_PRIMARY_KEY#name:TEXT#type:NUMERIC#image:BLOB#$owner:TRAINER#");
        }
        catch (IllegalArgumentException iae) {
            failureMessage = iae.getMessage();
        }

        assertEquals("Entity name not specified", failureMessage);
    }
}
