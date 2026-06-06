package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class PositionEntryTest {

    private static final Date START = new Date(1000000L);
    private static final Date END   = new Date(9000000L);

    private Organization org() {
        return new Organization("Assembleia", OrganizationNature.PUBLIC, OrganizationType.POLITICAL_PARTY);
    }

    // -------------------------------------------------------------------------
    // Construction – happy path
    // -------------------------------------------------------------------------

    @Test
    void ensureCreationWithAllFieldsWorks() {
        assertDoesNotThrow(() -> new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                60000, 5000, 2000, START, END));
    }

    @Test
    void ensureCreationWithNullEndDateWorks() {
        assertDoesNotThrow(() -> new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                60000, 0, 0, START, null));
    }

    @Test
    void ensureCreationWithZeroSalariesWorks() {
        assertDoesNotThrow(() -> new PositionEntry(org(), "Volunteer", PositionNature.SOCIAL,
                0, 0, 0, START, null));
    }

    // -------------------------------------------------------------------------
    // Construction – null / invalid guards
    // -------------------------------------------------------------------------

    @Test
    void ensureNullOrganizationThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(null, "Deputy", PositionNature.PUBLIC,
                        60000, 0, 0, START, null));
    }

    @Test
    void ensureNullFunctionDesignationThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(org(), null, PositionNature.PUBLIC,
                        60000, 0, 0, START, null));
    }

    @Test
    void ensureBlankFunctionDesignationThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(org(), "   ", PositionNature.PUBLIC,
                        60000, 0, 0, START, null));
    }

    @Test
    void ensureNullNatureThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(org(), "Deputy", null,
                        60000, 0, 0, START, null));
    }

    @Test
    void ensureNegativeGrossSalaryThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                        -1, 0, 0, START, null));
    }

    @Test
    void ensureNegativeSideIncomeConsultingThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                        0, -0.01, 0, START, null));
    }

    @Test
    void ensureNegativeSideIncomeBoardMembershipsThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                        0, 0, -500, START, null));
    }

    @Test
    void ensureNullStartDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                        60000, 0, 0, null, null));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    @Test
    void ensureGetOrganizationReturnsCorrectValue() {
        Organization o = org();
        PositionEntry pe = new PositionEntry(o, "Deputy", PositionNature.PUBLIC,
                60000, 5000, 2000, START, END);
        assertEquals(o, pe.getOrganization());
    }

    @Test
    void ensureGetFunctionDesignationReturnsCorrectValue() {
        PositionEntry pe = new PositionEntry(org(), "Minister", PositionNature.PUBLIC,
                80000, 0, 0, START, null);
        assertEquals("Minister", pe.getFunctionDesignation());
    }

    @Test
    void ensureGetNatureReturnsCorrectValue() {
        PositionEntry pe = new PositionEntry(org(), "Advisor", PositionNature.PRIVATE,
                0, 3000, 0, START, null);
        assertEquals(PositionNature.PRIVATE, pe.getNature());
    }

    @Test
    void ensureGetGrossSalaryReturnsCorrectValue() {
        PositionEntry pe = new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                55000, 0, 0, START, null);
        assertEquals(55000, pe.getGrossSalary(), 0.001);
    }

    @Test
    void ensureGetSideIncomeConsultingReturnsCorrectValue() {
        PositionEntry pe = new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                0, 7500, 0, START, null);
        assertEquals(7500, pe.getSideIncomeConsulting(), 0.001);
    }

    @Test
    void ensureGetSideIncomeBoardMembershipsReturnsCorrectValue() {
        PositionEntry pe = new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                0, 0, 3000, START, null);
        assertEquals(3000, pe.getSideIncomeBoardMemberships(), 0.001);
    }

    @Test
    void ensureGetStartDateReturnsCorrectValue() {
        PositionEntry pe = new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                0, 0, 0, START, null);
        assertEquals(START, pe.getStartDate());
    }

    @Test
    void ensureGetEndDateReturnsNullWhenNotSet() {
        PositionEntry pe = new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                0, 0, 0, START, null);
        assertNull(pe.getEndDate());
    }

    @Test
    void ensureGetEndDateReturnsCorrectValueWhenSet() {
        PositionEntry pe = new PositionEntry(org(), "Deputy", PositionNature.PUBLIC,
                0, 0, 0, START, END);
        assertEquals(END, pe.getEndDate());
    }
}
