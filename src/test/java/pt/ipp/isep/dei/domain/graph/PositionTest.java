package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void ensureFieldsAreKept() {
        Position position = new Position("J-001", "public", "2019-03-01", "2023-03-01",
                "Minister of Finance", "government", "O-001");
        assertEquals("J-001", position.id());
        assertEquals("public", position.type());
        assertEquals("2019-03-01", position.startDate());
        assertEquals("2023-03-01", position.endDate());
        assertEquals("Minister of Finance", position.positionTitle());
        assertEquals("government", position.positionType());
        assertEquals("O-001", position.organizationId());
    }

    @Test
    void ensureBlankIdsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Position("", "public", "", "", "Minister", "government", "O-001"));
        assertThrows(IllegalArgumentException.class,
                () -> new Position("   ", "public", "", "", "Minister", "government", "O-001"));
        assertThrows(IllegalArgumentException.class,
                () -> new Position(null, "public", "", "", "Minister", "government", "O-001"));
    }

    @Test
    void ensureNullStringsDefaultToEmpty() {
        Position position = new Position("J-002", "business", null, null, null, null, null);
        assertEquals("", position.startDate());
        assertEquals("", position.endDate());
        assertEquals("", position.positionTitle());
        assertEquals("", position.positionType());
        assertEquals("", position.organizationId());
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Position p1 = new Position("J-001", "public", "2019-03-01", "", "Minister", "government", "O-001");
        Position p2 = new Position("J-001", "business", "2015-01-01", "", "Board Member", "private", "O-002");
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, new Position("J-002", "public", "", "", "Minister", "government", "O-001"));
    }
}
