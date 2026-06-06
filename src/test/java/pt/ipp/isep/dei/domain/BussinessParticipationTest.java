package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessParticipationTest {

    private Organization org() {
        return new Organization("LusoTech SA", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
    }

    // -------------------------------------------------------------------------
    // Construction – happy path
    // -------------------------------------------------------------------------

    @Test
    void ensureCreationWorks() {
        assertDoesNotThrow(() -> new BusinessParticipation(org(), 123456789L, 15000.0, 10.0));
    }

    @Test
    void ensureCreationWithZeroValueWorks() {
        assertDoesNotThrow(() -> new BusinessParticipation(org(), 123456789L, 0.0, 0.0));
    }

    @Test
    void ensureCreationWithHundredPercentWorks() {
        assertDoesNotThrow(() -> new BusinessParticipation(org(), 123456789L, 50000.0, 100.0));
    }

    // -------------------------------------------------------------------------
    // Construction – guards
    // -------------------------------------------------------------------------

    @Test
    void ensureNullOrganizationThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new BusinessParticipation(null, 123456789L, 1000.0, 5.0));
    }

    @Test
    void ensureNegativeTotalValueThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new BusinessParticipation(org(), 123456789L, -0.01, 5.0));
    }

    @Test
    void ensureNegativePercentageThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new BusinessParticipation(org(), 123456789L, 1000.0, -0.01));
    }

    @Test
    void ensurePercentageAbove100Throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new BusinessParticipation(org(), 123456789L, 1000.0, 100.01));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    @Test
    void ensureGetOrganizationReturnsCorrectValue() {
        Organization o = org();
        BusinessParticipation bp = new BusinessParticipation(o, 111111111L, 5000.0, 5.0);
        assertEquals(o, bp.getOrganization());
    }

    @Test
    void ensureGetCompanyNIFReturnsCorrectValue() {
        BusinessParticipation bp = new BusinessParticipation(org(), 987654321L, 5000.0, 5.0);
        assertEquals(987654321L, bp.getCompanyNIF());
    }

    @Test
    void ensureGetTotalValueInStocksReturnsCorrectValue() {
        BusinessParticipation bp = new BusinessParticipation(org(), 123456789L, 25000.0, 10.0);
        assertEquals(25000.0, bp.getTotalValueInStocks(), 0.001);
    }

    @Test
    void ensureGetCompanyPercentageReturnsCorrectValue() {
        BusinessParticipation bp = new BusinessParticipation(org(), 123456789L, 25000.0, 12.5);
        assertEquals(12.5, bp.getCompanyPercentage(), 0.001);
    }
}