package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SubsidyEntryTest {

    private static final Date DATE = new Date(0);

    private Organization createOrg() {
        return new Organization("Foundation X", "public", OrganizationType.FOUNDATION);
    }

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    @Test
    void ensureSubsidyEntryCreationWorks() {
        SubsidyEntry se = new SubsidyEntry(createOrg(), 5000.0, "Research grant", DATE);
        assertNotNull(se);
    }

    @Test
    void ensureSubsidyEntryFailsWithNullOrganization() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(null, 5000.0, "Grant", DATE));
    }

    @Test
    void ensureSubsidyEntryFailsWithNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(createOrg(), -1.0, "Grant", DATE));
    }

    @Test
    void ensureSubsidyEntryFailsWithNullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(createOrg(), 5000.0, null, DATE));
    }

    @Test
    void ensureSubsidyEntryFailsWithBlankDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(createOrg(), 5000.0, "   ", DATE));
    }

    @Test
    void ensureSubsidyEntryFailsWithNullDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new SubsidyEntry(createOrg(), 5000.0, "Grant", null));
    }

    // -------------------------------------------------------------------------
    // Zero amount is valid (AC4)
    // -------------------------------------------------------------------------

    @Test
    void ensureZeroAmountIsValid() {
        SubsidyEntry se = new SubsidyEntry(createOrg(), 0.0, "In-kind support", DATE);
        assertEquals(0.0, se.getAmount());
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    @Test
    void ensureGettersReturnCorrectValues() {
        Organization org = createOrg();
        SubsidyEntry se = new SubsidyEntry(org, 1500.0, "Travel grant", DATE);

        assertEquals(org, se.getOrganization());
        assertEquals(1500.0, se.getAmount());
        assertEquals("Travel grant", se.getDescription());
        assertEquals(DATE, se.getDate());
    }
}
