package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EthicsCommitteeMemberTest {

    @Test
    void ensureCreationWorks() {
        EthicsCommitteeMember m = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        assertNotNull(m);
    }

    @Test
    void ensureCreationFailsWithNullName() {
        assertThrows(IllegalArgumentException.class, () ->
                new EthicsCommitteeMember(null, "maria@ethics.pt"));
    }

    @Test
    void ensureCreationFailsWithBlankName() {
        assertThrows(IllegalArgumentException.class, () ->
                new EthicsCommitteeMember("   ", "maria@ethics.pt"));
    }

    @Test
    void ensureCreationFailsWithNullEmail() {
        assertThrows(IllegalArgumentException.class, () ->
                new EthicsCommitteeMember("Maria Sousa", null));
    }

    @Test
    void ensureCreationFailsWithBlankEmail() {
        assertThrows(IllegalArgumentException.class, () ->
                new EthicsCommitteeMember("Maria Sousa", "   "));
    }

    @Test
    void ensureGettersReturnCorrectValues() {
        EthicsCommitteeMember m = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        assertEquals("Maria Sousa", m.getName());
        assertEquals("maria@ethics.pt", m.getEmail());
    }

    @Test
    void ensureHasEmailReturnsTrueForMatchingEmail() {
        EthicsCommitteeMember m = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        assertTrue(m.hasEmail("maria@ethics.pt"));
    }

    @Test
    void ensureHasEmailIsCaseInsensitive() {
        EthicsCommitteeMember m = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        assertTrue(m.hasEmail("MARIA@ETHICS.PT"));
    }

    @Test
    void ensureHasEmailReturnsFalseForDifferentEmail() {
        EthicsCommitteeMember m = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        assertFalse(m.hasEmail("other@ethics.pt"));
    }

    @Test
    void ensureEqualsReturnsTrueForSameEmail() {
        EthicsCommitteeMember m1 = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        EthicsCommitteeMember m2 = new EthicsCommitteeMember("Maria S.", "maria@ethics.pt");
        assertEquals(m1, m2);
    }

    @Test
    void ensureEqualsReturnsFalseForDifferentEmail() {
        EthicsCommitteeMember m1 = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        EthicsCommitteeMember m2 = new EthicsCommitteeMember("Maria Sousa", "other@ethics.pt");
        assertNotEquals(m1, m2);
    }
}
