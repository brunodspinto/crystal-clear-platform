package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PositionEntryTest {

    private static final Date START = new Date(0);
    private static final Date END = new Date(86400000); // +1 day

    private Organization createOrg() {
        return new Organization("TechCorp", "private", OrganizationType.COMPANY);
    }

    private String createFunction() {
        return "Director";
    }

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    @Test
    void ensurePositionEntryCreationWorks() {
        PositionEntry pe = new PositionEntry(createOrg(), createFunction(),
                PositionNature.PUBLIC, 50000, 5000, 3000, START, END);
        assertNotNull(pe);
    }

    @Test
    void ensurePositionEntryWithNullEndDateWorks() {
        PositionEntry pe = new PositionEntry(createOrg(), createFunction(),
                PositionNature.PRIVATE, 30000, 0, 0, START, null);
        assertNotNull(pe);
        assertNull(pe.getEndDate());
    }

    @Test
    void ensurePositionEntryFailsWithNullOrganization() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(null, createFunction(), PositionNature.PUBLIC, 50000, 0, 0, START, END));
    }

    @Test
    void ensurePositionEntryFailsWithNullFunction() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(createOrg(), null, PositionNature.PUBLIC, 50000, 0, 0, START, END));
    }

    @Test
    void ensurePositionEntryFailsWithNullNature() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(createOrg(), createFunction(), null, 50000, 0, 0, START, END));
    }

    @Test
    void ensurePositionEntryFailsWithNegativeGrossSalary() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(createOrg(), createFunction(), PositionNature.PUBLIC, -1, 0, 0, START, END));
    }

    @Test
    void ensurePositionEntryFailsWithNegativeSideIncomeConsulting() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(createOrg(), createFunction(), PositionNature.PUBLIC, 50000, -1, 0, START, END));
    }

    @Test
    void ensurePositionEntryFailsWithNegativeSideIncomeBoardMemberships() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(createOrg(), createFunction(), PositionNature.PUBLIC, 50000, 0, -1, START, END));
    }

    @Test
    void ensurePositionEntryFailsWithNullStartDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(createOrg(), createFunction(), PositionNature.PUBLIC, 50000, 0, 0, null, END));
    }

    // -------------------------------------------------------------------------
    // Zero values are valid (AC4)
    // -------------------------------------------------------------------------

    @Test
    void ensureZeroGrossSalaryIsValid() {
        PositionEntry pe = new PositionEntry(createOrg(), createFunction(),
                PositionNature.SOCIAL, 0, 0, 0, START, END);
        assertEquals(0, pe.getGrossSalary());
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    @Test
    void ensureGettersReturnCorrectValues() {
        Organization org = createOrg();
        String function = createFunction();
        PositionEntry pe = new PositionEntry(org, function, PositionNature.PRIVATE,
                40000, 2000, 1000, START, END);

        assertEquals(org, pe.getOrganization());
        assertEquals(function, pe.getFunctionDesignation());
        assertEquals(PositionNature.PRIVATE, pe.getNature());
        assertEquals(40000, pe.getGrossSalary());
        assertEquals(2000, pe.getSideIncomeConsulting());
        assertEquals(1000, pe.getSideIncomeBoardMemberships());
        assertEquals(START, pe.getStartDate());
        assertEquals(END, pe.getEndDate());
    }
}
