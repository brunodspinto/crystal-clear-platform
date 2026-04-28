package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void ensureFieldsAreKept() {
        Entity entity = new Entity("P-001", "Person", "Alice");
        assertEquals("P-001", entity.id());
        assertEquals("Person", entity.type());
        assertEquals("Alice", entity.name());
    }

    @Test
    void ensureBlankIdsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Entity("", "Person", "Alice"));
        assertThrows(IllegalArgumentException.class, () -> new Entity("   ", "Person", "Alice"));
        assertThrows(IllegalArgumentException.class, () -> new Entity(null, "Person", "Alice"));
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Entity e1 = new Entity("P-001", "Person", "Alice");
        Entity e2 = new Entity("P-001", "Organization", "Different Name");
        Entity e3 = new Entity("P-002", "Person", "Alice");

        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertEquals(e1.hashCode(), e2.hashCode());
    }
}
