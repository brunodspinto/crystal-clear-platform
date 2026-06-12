package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class CitizenTest {

    @Test
    void ensureCreationWorks() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertNotNull(c);
    }

    @Test
    void ensureCreationFailsWithNullEmail() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Citizen(null, "João Silva", "CC123456789");
            }
        });
    }

    @Test
    void ensureCreationFailsWithBlankEmail() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Citizen("   ", "João Silva", "CC123456789");
            }
        });
    }

    @Test
    void ensureCreationFailsWithNullName() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Citizen("citizen@test.pt", null, "CC123456789");
            }
        });
    }

    @Test
    void ensureCreationFailsWithBlankName() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Citizen("citizen@test.pt", "   ", "CC123456789");
            }
        });
    }

    @Test
    void ensureCreationFailsWithNullNationalIdCardNumber() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Citizen("citizen@test.pt", "João Silva", null);
            }
        });
    }

    @Test
    void ensureCreationFailsWithBlankNationalIdCardNumber() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Citizen("citizen@test.pt", "João Silva", "   ");
            }
        });
    }

    @Test
    void ensureGetNameReturnsCorrectValue() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertEquals("João Silva", c.getName());
    }

    @Test
    void ensureGetEmailReturnsCorrectValue() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertEquals("citizen@test.pt", c.getEmail());
    }

    @Test
    void ensureGetNationalIdCardNumberReturnsCorrectValue() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertEquals("CC123456789", c.getNationalIdCardNumber());
    }

    @Test
    void ensureHasEmailReturnsTrueForMatchingEmail() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertTrue(c.hasEmail("citizen@test.pt"));
    }

    @Test
    void ensureHasEmailIsCaseInsensitive() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertTrue(c.hasEmail("CITIZEN@TEST.PT"));
    }

    @Test
    void ensureHasEmailReturnsFalseForDifferentEmail() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertFalse(c.hasEmail("other@test.pt"));
    }

    @Test
    void ensureEqualsReturnsTrueForSameEmail() {
        Citizen c1 = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        Citizen c2 = new Citizen("citizen@test.pt", "João S.", "CC999999999");
        assertEquals(c1, c2);
    }

    @Test
    void ensureEqualsIsCaseInsensitive() {
        Citizen c1 = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        Citizen c2 = new Citizen("CITIZEN@TEST.PT", "João Silva", "CC123456789");
        assertEquals(c1, c2);
    }

    @Test
    void ensureEqualsReturnsFalseForDifferentEmail() {
        Citizen c1 = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        Citizen c2 = new Citizen("other@test.pt", "João Silva", "CC123456789");
        assertNotEquals(c1, c2);
    }

    @Test
    void ensureEqualsReturnsTrueForSameInstance() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertEquals(c, c);
    }

    @Test
    void ensureEqualsReturnsFalseForNull() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertNotEquals(null, c);
    }

    @Test
    void ensureEqualsReturnsFalseForDifferentType() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        assertNotEquals("citizen@test.pt", c);
    }

    @Test
    void ensureToStringContainsNameAndEmail() {
        Citizen c = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        String result = c.toString();
        assertTrue(result.contains("João Silva"));
        assertTrue(result.contains("citizen@test.pt"));
    }

    @Test
    void ensureCloneCreatesEqualCopy() {
        Citizen original = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        Citizen clone = original.clone();
        assertEquals(original, clone);
        assertEquals(original.getName(), clone.getName());
        assertEquals(original.getEmail(), clone.getEmail());
        assertEquals(original.getNationalIdCardNumber(), clone.getNationalIdCardNumber());
    }

    @Test
    void ensureCloneIsNotSameInstance() {
        Citizen original = new Citizen("citizen@test.pt", "João Silva", "CC123456789");
        Citizen clone = original.clone();
        assertNotSame(original, clone);
    }
}
