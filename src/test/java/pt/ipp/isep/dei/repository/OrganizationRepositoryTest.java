package pt.ipp.isep.dei.repository;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.Employee;
import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationNature;
import pt.ipp.isep.dei.domain.OrganizationType;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationRepositoryTest {

    @Test
    void testAddOrganization() {
        OrganizationRepository organizationRepository = new OrganizationRepository();
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");

        Organization returnOrganization = organizationRepository.add(organization);

        assertEquals(organization, returnOrganization);
    }

    @Test
    void ensureGetOrganizationByEmployeeWorks() {
        OrganizationRepository organizationRepository = new OrganizationRepository();
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee);
        organizationRepository.add(organization);

        Organization result = organizationRepository.getOrganizationByEmployee(employee);

        assertEquals(organization, result);
    }

    @Test
    void ensureGetOrganizationByEmployeeFails() {
        OrganizationRepository organizationRepository = new OrganizationRepository();
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee);
        organizationRepository.add(organization);

        Employee employee2 = new Employee("jane.doe@this.company.com");
        Organization result = organizationRepository.getOrganizationByEmployee(employee2);

        assertNull(result);
    }

    @Test
    void ensureGetOrganizationByEmailWorks() {
        OrganizationRepository organizationRepository = new OrganizationRepository();
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee);
        organizationRepository.add(organization);

        Organization result =
                organizationRepository.getOrganizationByEmployeeEmail("john.doe@this.company.com");

        assertEquals(organization, result);
    }

    @Test
    void ensureAddOrganizationWorks() {
        OrganizationRepository organizationRepository = new OrganizationRepository();
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee);

        organizationRepository.add(organization);

        Organization returnOrganization =
                organizationRepository.getOrganizationByEmployeeEmail("john.doe@this.company.com");

        //Assert
        //Make sure both represents the same object
        assertEquals(organization, returnOrganization);
        //Make sure it is a clone (different memory addresses)
        assertNotSame(organization, returnOrganization);
    }

    @Test
    void ensureAddOrganizationDuplicateFails() {
        OrganizationRepository organizationRepository = new OrganizationRepository();
        Organization organization = new Organization("123456789", "Test Org", "www.test.com", "912345678", "test@test.com");
        Employee employee = new Employee("john.doe@this.company.com");
        organization.addEmployee(employee);
        organizationRepository.add(organization);

        Organization result = organizationRepository.add(organization);

        assertNull(result);
    }

    // --- US04 Tests (save + existsByNameAndType) ---

    @Test
    void ensureSaveOrganizationWorks() {
        OrganizationRepository repo = new OrganizationRepository();
        Organization org = new Organization("ACME Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
        assertTrue(repo.save(org));
    }

    @Test
    void ensureSaveDuplicateOrganizationFails() {
        OrganizationRepository repo = new OrganizationRepository();
        Organization org = new Organization("ACME Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
        repo.save(org);
        Organization dup = new Organization("ACME Corp", OrganizationNature.PUBLIC, OrganizationType.COMPANY);
        assertFalse(repo.save(dup));
    }

    @Test
    void ensureSameNameDifferentTypeIsAllowed() {
        OrganizationRepository repo = new OrganizationRepository();
        Organization org1 = new Organization("ACME Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
        Organization org2 = new Organization("ACME Corp", OrganizationNature.SOCIAL, OrganizationType.FOUNDATION);
        repo.save(org1);
        assertTrue(repo.save(org2));
    }

    @Test
    void ensureExistsByNameAndTypeWorks() {
        OrganizationRepository repo = new OrganizationRepository();
        Organization org = new Organization("ACME Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
        repo.save(org);
        assertTrue(repo.existsByNameAndType("ACME Corp", OrganizationType.COMPANY));
    }

    @Test
    void ensureExistsByNameAndTypeIsCaseInsensitive() {
        OrganizationRepository repo = new OrganizationRepository();
        Organization org = new Organization("ACME Corp", OrganizationNature.SOCIAL, OrganizationType.FOUNDATION);
        repo.save(org);
        assertTrue(repo.existsByNameAndType("acme corp", OrganizationType.FOUNDATION));
    }

    @Test
    void ensureExistsByNameAndTypeReturnsFalseForDifferentType() {
        OrganizationRepository repo = new OrganizationRepository();
        Organization org = new Organization("ACME Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
        repo.save(org);
        assertFalse(repo.existsByNameAndType("ACME Corp", OrganizationType.FOUNDATION));
    }
}