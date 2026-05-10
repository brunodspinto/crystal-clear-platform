package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.UserRole;
import pt.ipp.isep.dei.repository.RegistrationRequestRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegisterControllerTest {

    private RegistrationRequestRepository repo;
    private RegisterController controller;

    @BeforeEach
    void setUp() {
        repo = new RegistrationRequestRepository();
        controller = new RegisterController(repo);
    }

    @Test
    void ensureGetAvailableRolesExcludesAdministrator() {
        List<UserRole> roles = controller.getAvailableRoles();
        assertFalse(roles.contains(UserRole.ADMINISTRATOR));
        assertEquals(UserRole.values().length - 1, roles.size());
    }

    @Test
    void ensureGetDocumentLabelForJournalist() {
        assertNotNull(controller.getDocumentLabel(UserRole.JOURNALIST));
        assertTrue(controller.getDocumentLabel(UserRole.JOURNALIST).toLowerCase().contains("press"));
    }

    @Test
    void ensureGetDocumentLabelForCitizen() {
        assertNotNull(controller.getDocumentLabel(UserRole.CITIZEN));
        assertTrue(controller.getDocumentLabel(UserRole.CITIZEN).toLowerCase().contains("national"));
    }

    @Test
    void ensureGetDocumentLabelForOtherRoleReturnsNull() {
        assertNull(controller.getDocumentLabel(UserRole.POLITICAL_AGENT));
        assertNull(controller.getDocumentLabel(UserRole.ETHICS_COMMITTEE));
    }

    @Test
    void ensureJournalistRequiresDocument() {
        assertTrue(controller.requiresDocument(UserRole.JOURNALIST));
    }

    @Test
    void ensureCitizenRequiresDocument() {
        assertTrue(controller.requiresDocument(UserRole.CITIZEN));
    }

    @Test
    void ensureAdminDoesNotRequireDocument() {
        assertFalse(controller.requiresDocument(UserRole.ADMINISTRATOR));
    }

    @Test
    void ensurePoliticalAgentDoesNotRequireDocument() {
        assertFalse(controller.requiresDocument(UserRole.POLITICAL_AGENT));
    }

    @Test
    void ensureValidRequestIsSubmitted() {
        boolean result = controller.submitRequest("John Doe", "john@mail.com", "AAA11bb",
                UserRole.ADMINISTRATOR, null);
        assertTrue(result);
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void ensureDuplicateRequestIsRejected() {
        controller.submitRequest("John Doe", "john@mail.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        boolean result = controller.submitRequest("John Doe", "john@mail.com", "AAA11bb",
                UserRole.ADMINISTRATOR, null);
        assertFalse(result);
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void ensureSameEmailDifferentRoleIsAllowed() {
        controller.submitRequest("John Doe", "john@mail.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        boolean result = controller.submitRequest("John Doe", "john@mail.com", "AAA11bb",
                UserRole.POLITICAL_AGENT, null);
        assertTrue(result);
        assertEquals(2, repo.getAll().size());
    }

    @Test
    void ensureInvalidPasswordThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.submitRequest("Name", "a@b.com", "weak", UserRole.ADMINISTRATOR, null));
    }

    @Test
    void ensureJournalistWithDocumentIsSubmitted() {
        boolean result = controller.submitRequest("Jane Press", "jane@news.pt", "AAA11bb",
                UserRole.JOURNALIST, "J-99999");
        assertTrue(result);
    }

    @Test
    void ensureJournalistWithoutDocumentThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.submitRequest("Jane Press", "jane@news.pt", "AAA11bb",
                        UserRole.JOURNALIST, null));
    }

    @Test
    void ensureIsValidPasswordDelegatesToDomain() {
        assertTrue(controller.isValidPassword("AAA11bb"));
        assertFalse(controller.isValidPassword("weak"));
    }
}
