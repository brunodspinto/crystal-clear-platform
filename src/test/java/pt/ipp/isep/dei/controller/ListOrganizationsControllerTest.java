package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationNature;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.repository.OrganizationRepository;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ListOrganizationsControllerTest {

    @Test
    void ensureEmptyRepositoryReturnsEmptyGroups() {
        OrganizationRepository repo = new OrganizationRepository();
        ListOrganizationsController controller = new ListOrganizationsController(repo);

        Map<OrganizationType, List<Organization>> result = controller.getOrganizationsGroupedByType();

        for (OrganizationType type : OrganizationType.values()) {
            assertTrue(result.get(type).isEmpty());
        }
    }

    @Test
    void ensureOrganizationAppearsInCorrectGroup() {
        OrganizationRepository repo = new OrganizationRepository();
        repo.save(new Organization("Parliament", OrganizationNature.PUBLIC, OrganizationType.INSTITUTE));
        ListOrganizationsController controller = new ListOrganizationsController(repo);

        Map<OrganizationType, List<Organization>> result = controller.getOrganizationsGroupedByType();

        assertEquals(1, result.get(OrganizationType.INSTITUTE).size());
        assertEquals("Parliament", result.get(OrganizationType.INSTITUTE).get(0).getName());
    }

    @Test
    void ensureOrganizationsAreSortedAlphabeticallyWithinGroup() {
        OrganizationRepository repo = new OrganizationRepository();
        repo.save(new Organization("Zebra Party", OrganizationNature.PUBLIC, OrganizationType.POLITICAL_PARTY));
        repo.save(new Organization("Alpha Party", OrganizationNature.PUBLIC, OrganizationType.POLITICAL_PARTY));
        repo.save(new Organization("Mango Party", OrganizationNature.PUBLIC, OrganizationType.POLITICAL_PARTY));
        ListOrganizationsController controller = new ListOrganizationsController(repo);

        List<Organization> parties = controller.getOrganizationsGroupedByType().get(OrganizationType.POLITICAL_PARTY);

        assertEquals("Alpha Party", parties.get(0).getName());
        assertEquals("Mango Party", parties.get(1).getName());
        assertEquals("Zebra Party", parties.get(2).getName());
    }

    @Test
    void ensureOrganizationsOfDifferentTypesAreInSeparateGroups() {
        OrganizationRepository repo = new OrganizationRepository();
        repo.save(new Organization("Tech Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY));
        repo.save(new Organization("Green Foundation", OrganizationNature.SOCIAL, OrganizationType.FOUNDATION));
        ListOrganizationsController controller = new ListOrganizationsController(repo);

        Map<OrganizationType, List<Organization>> result = controller.getOrganizationsGroupedByType();

        assertEquals(1, result.get(OrganizationType.COMPANY).size());
        assertEquals(1, result.get(OrganizationType.FOUNDATION).size());
        assertEquals("Tech Corp", result.get(OrganizationType.COMPANY).get(0).getName());
        assertEquals("Green Foundation", result.get(OrganizationType.FOUNDATION).get(0).getName());
    }

    @Test
    void ensureAllTypesAlwaysPresentInResult() {
        OrganizationRepository repo = new OrganizationRepository();
        ListOrganizationsController controller = new ListOrganizationsController(repo);

        Map<OrganizationType, List<Organization>> result = controller.getOrganizationsGroupedByType();

        for (OrganizationType type : OrganizationType.values()) {
            assertTrue(result.containsKey(type));
        }
    }

    @Test
    void ensureSortingIsCaseInsensitive() {
        OrganizationRepository repo = new OrganizationRepository();
        repo.save(new Organization("zebra Assoc", OrganizationNature.SOCIAL, OrganizationType.ASSOCIATION));
        repo.save(new Organization("Alpha Assoc", OrganizationNature.SOCIAL, OrganizationType.ASSOCIATION));
        ListOrganizationsController controller = new ListOrganizationsController(repo);

        List<Organization> assocs = controller.getOrganizationsGroupedByType().get(OrganizationType.ASSOCIATION);

        assertEquals("Alpha Assoc", assocs.get(0).getName());
        assertEquals("zebra Assoc", assocs.get(1).getName());
    }
}
