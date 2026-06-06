package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class IncomeTest {

    private Organization createOrg() {
        return new Organization("TechCorp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
    }

    @Test
    void ensureCreationWorks() {
        Income income = new Income(createOrg(), 5000.0, "Consulting", new Date());
        assertNotNull(income);
    }

    @Test
    void ensureCreationFailsWithNullOrganization() {
        assertThrows(IllegalArgumentException.class, () ->
                new Income(null, 5000.0, "Consulting", new Date()));
    }

    @Test
    void ensureCreationFailsWithNullSource() {
        assertThrows(IllegalArgumentException.class, () ->
                new Income(createOrg(), 5000.0, null, new Date()));
    }

    @Test
    void ensureCreationFailsWithBlankSource() {
        assertThrows(IllegalArgumentException.class, () ->
                new Income(createOrg(), 5000.0, "   ", new Date()));
    }

    @Test
    void ensureCreationFailsWithNullDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new Income(createOrg(), 5000.0, "Consulting", null));
    }

    @Test
    void ensureCreationFailsWithNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                new Income(createOrg(), -1.0, "Consulting", new Date()));
    }

    @Test
    void ensureZeroAmountIsAllowed() {
        Income income = new Income(createOrg(), 0.0, "Consulting", new Date());
        assertEquals(0.0, income.getAmount());
    }

    @Test
    void ensureGetOrganizationReturnsCorrectValue() {
        Organization org = createOrg();
        Income income = new Income(org, 5000.0, "Consulting", new Date());
        assertEquals(org, income.getOrganization());
    }

    @Test
    void ensureGetAmountReturnsCorrectValue() {
        Income income = new Income(createOrg(), 5000.0, "Consulting", new Date());
        assertEquals(5000.0, income.getAmount());
    }

    @Test
    void ensureGetSourceReturnsCorrectValue() {
        Income income = new Income(createOrg(), 5000.0, "Consulting", new Date());
        assertEquals("Consulting", income.getSource());
    }

    @Test
    void ensureGetDateReturnsCorrectValue() {
        Date date = new Date();
        Income income = new Income(createOrg(), 5000.0, "Consulting", date);
        assertEquals(date, income.getDate());
    }

    @Test
    void ensureToStringContainsKeyInfo() {
        Income income = new Income(createOrg(), 5000.0, "Consulting", new Date());
        String result = income.toString();
        assertTrue(result.contains("TechCorp"));
        assertTrue(result.contains("5000"));
        assertTrue(result.contains("Consulting"));
    }
}
