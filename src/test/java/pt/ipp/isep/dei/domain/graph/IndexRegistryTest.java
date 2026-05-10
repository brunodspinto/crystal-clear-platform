package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexRegistryTest {

    @Test
    void ensureFirstIdGetsIndexZero() {
        IndexRegistry r = new IndexRegistry();
        assertEquals(0, r.indexFor("A"));
    }

    @Test
    void ensureNewIdsGetSequentialIndexes() {
        IndexRegistry r = new IndexRegistry();
        assertEquals(0, r.indexFor("A"));
        assertEquals(1, r.indexFor("B"));
        assertEquals(2, r.indexFor("C"));
    }

    @Test
    void ensureSameIdReturnsSameIndex() {
        IndexRegistry r = new IndexRegistry();
        r.indexFor("A");
        r.indexFor("B");
        assertEquals(0, r.indexFor("A"));
        assertEquals(1, r.indexFor("B"));
    }

    @Test
    void ensureSizeReflectsRegisteredIds() {
        IndexRegistry r = new IndexRegistry();
        assertEquals(0, r.size());
        r.indexFor("A");
        r.indexFor("B");
        assertEquals(2, r.size());
        r.indexFor("A");
        assertEquals(2, r.size());
    }

    @Test
    void ensureIdAtReturnsRegisteredId() {
        IndexRegistry r = new IndexRegistry();
        r.indexFor("A");
        r.indexFor("B");
        assertEquals("A", r.idAt(0));
        assertEquals("B", r.idAt(1));
    }

    @Test
    void ensureIdAtRejectsOutOfRange() {
        IndexRegistry r = new IndexRegistry();
        r.indexFor("A");
        assertThrows(IndexOutOfBoundsException.class, () -> r.idAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> r.idAt(1));
    }

    @Test
    void ensureContainsReportsRegisteredIds() {
        IndexRegistry r = new IndexRegistry();
        r.indexFor("A");
        assertTrue(r.contains("A"));
        assertFalse(r.contains("B"));
        assertFalse(r.contains(null));
    }

    @Test
    void ensureBlankIdIsRejected() {
        IndexRegistry r = new IndexRegistry();
        assertThrows(IllegalArgumentException.class, () -> r.indexFor(null));
        assertThrows(IllegalArgumentException.class, () -> r.indexFor(""));
        assertThrows(IllegalArgumentException.class, () -> r.indexFor("  "));
    }
}
