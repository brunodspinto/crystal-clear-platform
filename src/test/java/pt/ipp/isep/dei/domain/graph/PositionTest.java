package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void ensureFieldsAreKept() {
        Position position = new Position("J-001", "public", "2019-03-01", "2023-03-01",
                "Minister of Finance", "government", "O-001");
        assertEquals("J-001", position.getId());
        assertEquals("public", position.getType());
        assertEquals("2019-03-01", position.getStartDate());
        assertEquals("2023-03-01", position.getEndDate());
        assertEquals("Minister of Finance", position.getPositionTitle());
        assertEquals("government", position.getPositionType());
        assertEquals("O-001", position.getOrganizationId());
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
        assertEquals("", position.getStartDate());
        assertEquals("", position.getEndDate());
        assertEquals("", position.getPositionTitle());
        assertEquals("", position.getPositionType());
        assertEquals("", position.getOrganizationId());
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Position p1 = new Position("J-001", "public", "2019-03-01", "", "Minister", "government", "O-001");
        Position p2 = new Position("J-001", "business", "2015-01-01", "", "Board Member", "private", "O-002");
        assertEquals(p1, p2);
        assertNotEquals(p1, new Position("J-002", "public", "", "", "Minister", "government", "O-001"));
    }
}
