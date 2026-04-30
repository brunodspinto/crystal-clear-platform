package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void ensureFieldsAreKept() {
        Person person = new Person("P-001", "politician", "2020-01-01", "2024-01-01",
                "Alice Smith", "1980-05-10", "Portuguese");
        assertEquals("P-001", person.getId());
        assertEquals("politician", person.getType());
        assertEquals("2020-01-01", person.getStartDate());
        assertEquals("2024-01-01", person.getEndDate());
        assertEquals("Alice Smith", person.getName());
        assertEquals("1980-05-10", person.getBirthDate());
        assertEquals("Portuguese", person.getNationality());
    }

    @Test
    void ensureBlankIdsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Person("", "politician", "", "", "Alice", "", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new Person("   ", "politician", "", "", "Alice", "", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new Person(null, "politician", "", "", "Alice", "", ""));
    }

    @Test
    void ensureNullStringsDefaultToEmpty() {
        Person person = new Person("P-002", "advisor", null, null, null, null, null);
        assertEquals("", person.getStartDate());
        assertEquals("", person.getEndDate());
        assertEquals("", person.getName());
        assertEquals("", person.getBirthDate());
        assertEquals("", person.getNationality());
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Person p1 = new Person("P-001", "politician", "2020-01-01", "", "Alice", "", "Portuguese");
        Person p2 = new Person("P-001", "businessman", "2018-01-01", "", "Different Name", "", "Spanish");
        assertEquals(p1, p2);
        assertNotEquals(p1, new Person("P-002", "politician", "", "", "Alice", "", ""));
    }
}
