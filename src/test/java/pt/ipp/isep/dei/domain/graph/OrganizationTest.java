package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class OrganizationTest {

    @Test
    void ensureFieldsAreKept() {
        Organization org = new Organization("O-001", "company", "2010-01-01", "2024-01-01",
                "Acme Corp", "private", "Portugal");
        assertEquals("O-001", org.getId());
        assertEquals("company", org.getType());
        assertEquals("2010-01-01", org.getStartDate());
        assertEquals("2024-01-01", org.getEndDate());
        assertEquals("Acme Corp", org.getName());
        assertEquals("private", org.getOrganizationType());
        assertEquals("Portugal", org.getCountry());
    }

    @Test
    void ensureBlankIdsAreRejected() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Organization("", "company", "", "", "Acme", "private", "PT");
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Organization("   ", "company", "", "", "Acme", "private", "PT");
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Organization(null, "company", "", "", "Acme", "private", "PT");
            }
        });
    }

    @Test
    void ensureNullStringsDefaultToEmpty() {
        Organization org = new Organization("O-002", "NGO", null, null, null, null, null);
        assertEquals("", org.getStartDate());
        assertEquals("", org.getEndDate());
        assertEquals("", org.getName());
        assertEquals("", org.getOrganizationType());
        assertEquals("", org.getCountry());
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Organization o1 = new Organization("O-001", "company", "2010-01-01", "", "Acme Corp", "private", "PT");
        Organization o2 = new Organization("O-001", "NGO", "2005-01-01", "", "Other Name", "foundation", "ES");
        assertEquals(o1, o2);
        assertNotEquals(o1, new Organization("O-002", "company", "", "", "Acme Corp", "private", "PT"));
    }
}
