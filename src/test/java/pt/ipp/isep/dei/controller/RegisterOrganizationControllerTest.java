package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.OrganizationNature;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.repository.OrganizationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class RegisterOrganizationControllerTest {

    @Test
    void ensureGetOrganizationTypesReturnsAllValues() {
        RegisterOrganizationController controller =
                new RegisterOrganizationController(new OrganizationRepository());

        List<OrganizationType> types = controller.getOrganizationTypes();

        assertEquals(OrganizationType.values().length, types.size());
    }

    @Test
    void ensureGetOrganizationNaturesReturnsAllValues() {
        RegisterOrganizationController controller =
                new RegisterOrganizationController(new OrganizationRepository());

        List<OrganizationNature> natures = controller.getOrganizationNatures();

        assertEquals(OrganizationNature.values().length, natures.size());
    }

    @Test
    void ensureGetOrganizationNaturesContainsAllNatures() {
        RegisterOrganizationController controller =
                new RegisterOrganizationController(new OrganizationRepository());

        List<OrganizationNature> natures = controller.getOrganizationNatures();

        assertTrue(natures.contains(OrganizationNature.PUBLIC));
        assertTrue(natures.contains(OrganizationNature.PRIVATE));
        assertTrue(natures.contains(OrganizationNature.SOCIAL));
    }

    @Test
    void ensureRegisterOrganizationWorksWithValidData() {
        RegisterOrganizationController controller =
                new RegisterOrganizationController(new OrganizationRepository());

        boolean result = controller.registerOrganization("ACME Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);

        assertTrue(result);
    }

    @Test
    void ensureRegisterOrganizationFailsForDuplicate() {
        OrganizationRepository repo = new OrganizationRepository();
        RegisterOrganizationController controller = new RegisterOrganizationController(repo);

        controller.registerOrganization("ACME Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
        boolean second = controller.registerOrganization("ACME Corp", OrganizationNature.PUBLIC, OrganizationType.COMPANY);

        assertFalse(second);
    }

    @Test
    void ensureRegisterOrganizationAllowsSameNameDifferentType() {
        OrganizationRepository repo = new OrganizationRepository();
        RegisterOrganizationController controller = new RegisterOrganizationController(repo);

        controller.registerOrganization("ACME Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
        boolean second = controller.registerOrganization("ACME Corp", OrganizationNature.SOCIAL, OrganizationType.FOUNDATION);

        assertTrue(second);
    }

    @Test
    void ensureRegisterOrganizationIsCaseInsensitiveForDuplicateDetection() {
        OrganizationRepository repo = new OrganizationRepository();
        RegisterOrganizationController controller = new RegisterOrganizationController(repo);

        controller.registerOrganization("ACME Corp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
        boolean second = controller.registerOrganization("acme corp", OrganizationNature.PUBLIC, OrganizationType.COMPANY);

        assertFalse(second);
    }

    @Test
    void ensureRegisterOrganizationFailsWithNullName() {
        RegisterOrganizationController controller =
                new RegisterOrganizationController(new OrganizationRepository());

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.registerOrganization(null, OrganizationNature.PRIVATE, OrganizationType.COMPANY);
            }
        });
    }

    @Test
    void ensureRegisterOrganizationFailsWithNullType() {
        RegisterOrganizationController controller =
                new RegisterOrganizationController(new OrganizationRepository());

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.registerOrganization("ACME Corp", OrganizationNature.PRIVATE, null);
            }
        });
    }
}
