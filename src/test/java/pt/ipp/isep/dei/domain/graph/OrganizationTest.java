package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationTest {

    @Test
    void ensureFieldsAreKept() {
        Organization org = new Organization("O-001", "company", "2010-01-01", "2024-01-01",
                "Acme Corp", "private", "Portugal");
        assertEquals("O-001", org.id());
        assertEquals("company", org.type());
        assertEquals("2010-01-01", org.startDate());
        assertEquals("2024-01-01", org.endDate());
        assertEquals("Acme Corp", org.name());
        assertEquals("private", org.organizationType());
        assertEquals("Portugal", org.country());
    }

    @Test
    void ensureBlankIdsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Organization("", "company", "", "", "Acme", "private", "PT"));
        assertThrows(IllegalArgumentException.class,
                () -> new Organization("   ", "company", "", "", "Acme", "private", "PT"));
        assertThrows(IllegalArgumentException.class,
                () -> new Organization(null, "company", "", "", "Acme", "private", "PT"));
    }

    @Test
    void ensureNullStringsDefaultToEmpty() {
        Organization org = new Organization("O-002", "NGO", null, null, null, null, null);
        assertEquals("", org.startDate());
        assertEquals("", org.endDate());
        assertEquals("", org.name());
        assertEquals("", org.organizationType());
        assertEquals("", org.country());
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Organization o1 = new Organization("O-001", "company", "2010-01-01", "", "Acme Corp", "private", "PT");
        Organization o2 = new Organization("O-001", "NGO", "2005-01-01", "", "Other Name", "foundation", "ES");
        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
        assertNotEquals(o1, new Organization("O-002", "company", "", "", "Acme Corp", "private", "PT"));
    }
}
