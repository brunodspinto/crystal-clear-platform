package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SubsidyEntryTest {

    private static final Date DATE = new Date();

    private Organization org() {
        return new Organization("Foundation", OrganizationNature.PUBLIC, OrganizationType.FOUNDATION);
    }

    // -------------------------------------------------------------------------
    // Construction – happy path
    // -------------------------------------------------------------------------

    @Test
    void ensureCreationWorks() {
        assertDoesNotThrow(() -> new SubsidyEntry(org(), 1500.0, "Research grant", DATE));
    }

    @Test
    void ensureCreationWithZeroAmountWorks() {
        assertDoesNotThrow(() -> new SubsidyEntry(org(), 0.0, "No-cost grant", DATE));
    }

    // -------------------------------------------------------------------------
    // Construction – guards
    // -------------------------------------------------------------------------

    @Test
    void ensureNullOrganizationThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(null, 1500.0, "Grant", DATE));
    }

    @Test
    void ensureNegativeAmountThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(org(), -0.01, "Grant", DATE));
    }

    @Test
    void ensureNullDescriptionThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(org(), 1000.0, null, DATE));
    }

    @Test
    void ensureBlankDescriptionThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(org(), 1000.0, "   ", DATE));
    }

    @Test
    void ensureNullDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(org(), 1000.0, "Grant", null));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    @Test
    void ensureGetOrganizationReturnsCorrectValue() {
        Organization o = org();
        SubsidyEntry se = new SubsidyEntry(o, 500.0, "Prize", DATE);
        assertEquals(o, se.getOrganization());
    }

    @Test
    void ensureGetAmountReturnsCorrectValue() {
        SubsidyEntry se = new SubsidyEntry(org(), 2500.0, "Grant", DATE);
        assertEquals(2500.0, se.getAmount(), 0.001);
    }

    @Test
    void ensureGetDescriptionReturnsCorrectValue() {
        SubsidyEntry se = new SubsidyEntry(org(), 100.0, "Travel subsidy", DATE);
        assertEquals("Travel subsidy", se.getDescription());
    }

    @Test
    void ensureGetDateReturnsCorrectValue() {
        SubsidyEntry se = new SubsidyEntry(org(), 100.0, "Grant", DATE);
        assertEquals(DATE, se.getDate());
    }
}
