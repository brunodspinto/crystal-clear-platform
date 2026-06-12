package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class EntityTest {

    @Test
    void ensureFieldsAreKept() {
        Person person = new Person("P-001", "politician", "2020-01-01", "2024-01-01",
                "Alice", "1980-05-10", "Portuguese");
        assertEquals("P-001", person.getId());
        assertEquals("politician", person.getType());
        assertEquals("2020-01-01", person.getStartDate());
        assertEquals("2024-01-01", person.getEndDate());
    }

    @Test
    void ensureBlankIdsAreRejected() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Person("", "politician", "", "", "Alice", "", "");
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Person("   ", "politician", "", "", "Alice", "", "");
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Person(null, "politician", "", "", "Alice", "", "");
            }
        });
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Person p1 = new Person("P-001", "politician", "2020-01-01", "", "Alice", "", "Portuguese");
        Person p2 = new Person("P-001", "businessman", "2018-01-01", "", "Different Name", "", "Spanish");
        Organization o1 = new Organization("P-001", "company", "", "", "Some Org", "NGO", "PT");

        assertEquals(p1, p2);
        assertNotEquals(p1, o1);
        assertNotEquals(p1, new Person("P-002", "politician", "", "", "Alice", "", ""));
    }
}
