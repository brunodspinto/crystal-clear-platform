package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessParticipationTest {

    private Organization createOrg() {
        return new Organization("TechCorp", "private", OrganizationType.COMPANY);
    }

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    @Test
    void ensureBusinessParticipationCreationWorks() {
        BusinessParticipation bp = new BusinessParticipation(createOrg(), 123456789L, 10000.0, 5.0);
        assertNotNull(bp);
    }

    @Test
    void ensureBusinessParticipationFailsWithNullOrganization() {
        assertThrows(IllegalArgumentException.class, () ->
                new BusinessParticipation(null, 123456789L, 10000.0, 5.0));
    }

    @Test
    void ensureBusinessParticipationFailsWithNegativeTotalValue() {
        assertThrows(IllegalArgumentException.class, () ->
                new BusinessParticipation(createOrg(), 123456789L, -1.0, 5.0));
    }

    @Test
    void ensureBusinessParticipationFailsWithNegativePercentage() {
        assertThrows(IllegalArgumentException.class, () ->
                new BusinessParticipation(createOrg(), 123456789L, 10000.0, -1.0));
    }

    @Test
    void ensureBusinessParticipationFailsWithPercentageAbove100() {
        assertThrows(IllegalArgumentException.class, () ->
                new BusinessParticipation(createOrg(), 123456789L, 10000.0, 101.0));
    }

    // -------------------------------------------------------------------------
    // Boundary values (AC4)
    // -------------------------------------------------------------------------

    @Test
    void ensureZeroTotalValueIsValid() {
        BusinessParticipation bp = new BusinessParticipation(createOrg(), 123456789L, 0.0, 0.0);
        assertEquals(0.0, bp.getTotalValueInStocks());
    }

    @Test
    void ensureHundredPercentIsValid() {
        BusinessParticipation bp = new BusinessParticipation(createOrg(), 123456789L, 50000.0, 100.0);
        assertEquals(100.0, bp.getCompanyPercentage());
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    @Test
    void ensureGettersReturnCorrectValues() {
        Organization org = createOrg();
        BusinessParticipation bp = new BusinessParticipation(org, 987654321L, 25000.0, 12.5);

        assertEquals(org, bp.getOrganization());
        assertEquals(987654321L, bp.getCompanyNIF());
        assertEquals(25000.0, bp.getTotalValueInStocks());
        assertEquals(12.5, bp.getCompanyPercentage());
    }
}
