package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Tests for US04 constructor (name, nature, type) are at the bottom of this file.

class OrganizationTest {

    @Test
    void testEqualsSameObject() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        assertEquals(organization, organization);
    }

    @Test
    void testEqualsDifferentClass() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        assertNotEquals("", organization);
    }

    @Test
    void testEqualsNull() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        assertNotEquals(null, organization);
    }

    @Test
    void testEqualsDifferentObject() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Organization organization1 = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        assertEquals(organization, organization1);
    }

    @Test
    void ensureEqualsFailsForDifferentObjectType() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Organization organization1 = new Organization("123456788", "Test Org", "www.test.com", "912345678", "test@test.com");
        assertNotEquals(organization, organization1);
    }

    @Test
    void ensureEqualsFailsWhenComparingNull() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        assertNotEquals(organization, null);
    }

    @Test
    void ensureEqualsSuccessWhenComparingSameObject() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        assertEquals(organization, organization);
    }

    @Test
    void testThatCreateTaskWorks() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");

        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee); // CORREÇÃO: Adicionar o empregado primeiro

        TaskCategory taskCategory = new TaskCategory("Task Category Description");

        Task expected = new Task("Task Description", "Task Category Description", "informal description",
                "technical description", 1, 1d, taskCategory, employee);

        Task task =
                organization.createTask("Task Description", "Task Category Description", "informal description",
                        "technical description", 1, 1d, taskCategory, employee);

        assertNotNull(task);
        assertEquals(expected, task);
    }

    @Test
    void ensureAddingDuplicateTaskFails() {
        //Arrange
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee); // CORREÇÃO: Adicionar o empregado primeiro

        TaskCategory taskCategory = new TaskCategory("Task Category Description");

        //Add the first task
        Task originalTask =
                organization.createTask("Task Description", "Task Category Description", "informal description",
                        "technical description", 1, 1d, taskCategory, employee);

        //Act
        Task duplicateTask =
                organization.createTask("Task Description", "Task Category Description", "informal description",
                        "technical description", 1, 1d, taskCategory, employee);

        //Assert
        assertNull(duplicateTask);
    }


    @Test
    void ensureEmploysFails() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");

        assertFalse(organization.employs(employee));

    }

    @Test
    void ensureEmploysSuccess() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee);
        assertTrue(organization.employs(employee));
    }

    @Test
    void ensureAnyEmployeeHasEmailFails() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee);
        assertFalse(organization.anyEmployeeHasEmail("jane.doe@this.company.com"));
    }

    @Test
    void ensureAnyEmployeeHasEmailWorks() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee);
        assertTrue(organization.anyEmployeeHasEmail("john.doe@this.company.com"));
    }

    @Test
    void ensureAddDuplicateEmployeeFails() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        assertTrue(organization.addEmployee(employee));
        assertFalse(organization.addEmployee(employee));
    }

    @Test
    void ensureAddEmployeeWorks() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        assertTrue(organization.addEmployee(employee));
    }

    @Test
    void ensureCloneWorks() {
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee);
        organization.createTask("Task Description", "Task Category Description", "informal description",
                "technical description", 1, 1d, new TaskCategory("Task Category Description"), employee);

        Organization clone = organization.clone();
        assertEquals(organization, clone);
    }

    // --- US04 Tests (Organization with name, nature, type) ---

    @Test
    void ensureOrganizationUS04CreationWorks() {
        Organization org = new Organization("Test Org", "private", OrganizationType.COMPANY);
        assertEquals("Test Org", org.getName());
        assertEquals(OrganizationType.COMPANY, org.getType());
        assertEquals("private", org.getNature());
    }

    @Test
    void ensureOrganizationUS04CreationFailsWithNullName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization(null, "nature", OrganizationType.COMPANY));
    }

    @Test
    void ensureOrganizationUS04CreationFailsWithBlankName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization("   ", "nature", OrganizationType.COMPANY));
    }

    @Test
    void ensureOrganizationUS04CreationFailsWithNullType() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization("Test Org", "nature", null));
    }

    @Test
    void ensureOrganizationUS04CreationFailsWithNullNature() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization("Test Org", null, OrganizationType.FOUNDATION));
    }

    @Test
    void ensureOrganizationUS04CreationFailsWithBlankNature() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization("Test Org", "   ", OrganizationType.FOUNDATION));
    }

    @Test
    void ensureOrganizationUS04CloneWorks() {
        Organization org = new Organization("Test Org", "social", OrganizationType.FOUNDATION);
        Organization clone = org.clone();
        assertEquals(org.getName(), clone.getName());
        assertEquals(org.getType(), clone.getType());
        assertEquals(org.getNature(), clone.getNature());
    }
}