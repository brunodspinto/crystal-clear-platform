package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;

public class AuthenticationController {

    /** Role identifier for administrators. */
    public static final String ROLE_ADMIN = "ADMINISTRATOR";
    /** Role identifier for employees. */
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    /** Role identifier for citizens (US12). */
    public static final String ROLE_CITIZEN = "CITIZEN";
    /** Role identifier for political agents (US06). */
    public static final String ROLE_POLITICAL_AGENT = "POLITICAL_AGENT";

    //private final ApplicationSession applicationSession;
    private final AuthenticationRepository authenticationRepository;

    public AuthenticationController() {
        this.authenticationRepository = Repositories.getInstance().getAuthenticationRepository();
    }

    public boolean doLogin(String email, String pwd) {
        try {
            return authenticationRepository.doLogin(email, pwd);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public List<UserRoleDTO> getUserRoles() {
        if (authenticationRepository.getCurrentUserSession().isLoggedIn()) {
            return authenticationRepository.getCurrentUserSession().getUserRoles();
        }
        return null;
    }

    public void doLogout() {
        authenticationRepository.doLogout();
    }
}