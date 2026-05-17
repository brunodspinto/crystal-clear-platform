package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest {

    private AuthenticationController controller;
    private AuthenticationRepository authRepo;

    @BeforeEach
    void setUp() {
        authRepo = Repositories.getInstance().getAuthenticationRepository();
        authRepo.doLogout();
        controller = new AuthenticationController();
    }

    @Test
    void ensureRoleAdminConstantIsCorrect() {
        assertEquals("ADMINISTRATOR", AuthenticationController.ROLE_ADMIN);
    }

    @Test
    void ensureRoleCitizenConstantIsCorrect() {
        assertEquals("CITIZEN", AuthenticationController.ROLE_CITIZEN);
    }

    @Test
    void ensureRolePoliticalAgentConstantIsCorrect() {
        assertEquals("POLITICAL_AGENT", AuthenticationController.ROLE_POLITICAL_AGENT);
    }

    @Test
    void ensureRoleJournalistConstantIsCorrect() {
        assertEquals("JOURNALIST", AuthenticationController.ROLE_JOURNALIST);
    }

    @Test
    void ensureRoleEthicsCommitteeConstantIsCorrect() {
        assertEquals("ETHICS_COMMITTEE", AuthenticationController.ROLE_ETHICS_COMMITTEE);
    }

    @Test
    void ensureLoginWithWrongCredentialsReturnsFalse() {
        assertFalse(controller.doLogin("nobody@test.com", "wrongpass"));
    }

    @Test
    void ensureLoginWithNullEmailReturnsFalse() {
        assertFalse(controller.doLogin(null, "somepass"));
    }

    @Test
    void ensureGetUserRolesReturnsNullWhenNotLoggedIn() {
        assertNull(controller.getUserRoles());
    }

    @Test
    void ensureLoginWithRegisteredUserReturnsTrue() {
        authRepo.addUserRole(AuthenticationController.ROLE_ADMIN, "Administrator");
        authRepo.addUserWithRole("Admin Test", "admin.auth.test@test.com", "Admin123*", AuthenticationController.ROLE_ADMIN);

        assertTrue(controller.doLogin("admin.auth.test@test.com", "Admin123*"));
        controller.doLogout();
    }

    @Test
    void ensureGetUserRolesReturnsListAfterLogin() {
        authRepo.addUserRole(AuthenticationController.ROLE_ADMIN, "Administrator");
        authRepo.addUserWithRole("Admin Test2", "admin.auth2.test@test.com", "Admin123*", AuthenticationController.ROLE_ADMIN);

        controller.doLogin("admin.auth2.test@test.com", "Admin123*");
        assertNotNull(controller.getUserRoles());
        assertFalse(controller.getUserRoles().isEmpty());
        controller.doLogout();
    }

    @Test
    void ensureGetUserRolesReturnsNullAfterLogout() {
        authRepo.addUserRole(AuthenticationController.ROLE_ADMIN, "Administrator");
        authRepo.addUserWithRole("Admin Test3", "admin.auth3.test@test.com", "Admin123*", AuthenticationController.ROLE_ADMIN);

        controller.doLogin("admin.auth3.test@test.com", "Admin123*");
        controller.doLogout();
        assertNull(controller.getUserRoles());
    }
}
