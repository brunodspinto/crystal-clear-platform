package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void ensureFieldsAreKept() {
        Person person = new Person("P-001", "politician", "2020-01-01", "2024-01-01",
                "Alice Smith", "1980-05-10", "Portuguese");
        assertEquals("P-001", person.id());
        assertEquals("politician", person.type());
        assertEquals("2020-01-01", person.startDate());
        assertEquals("2024-01-01", person.endDate());
        assertEquals("Alice Smith", person.name());
        assertEquals("1980-05-10", person.birthDate());
        assertEquals("Portuguese", person.nationality());
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
        assertEquals("", person.startDate());
        assertEquals("", person.endDate());
        assertEquals("", person.name());
        assertEquals("", person.birthDate());
        assertEquals("", person.nationality());
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Person p1 = new Person("P-001", "politician", "2020-01-01", "", "Alice", "", "Portuguese");
        Person p2 = new Person("P-001", "businessman", "2018-01-01", "", "Different Name", "", "Spanish");
        assertEquals(p1, p2);
        assertNotEquals(p1, new Person("P-002", "politician", "", "", "Alice", "", ""));
    }
}
