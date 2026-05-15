package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;

/**
 * The type Authentication controller.
 */
public class AuthenticationController {

    /**
     * Role identifier for administrators.
     */
    public static final String ROLE_ADMIN = "ADMINISTRATOR";
    /**
     * Role identifier for citizens (US12).
     */
    public static final String ROLE_CITIZEN = "CITIZEN";
    /**
     * Role identifier for political agents (US06).
     */
    public static final String ROLE_POLITICAL_AGENT = "POLITICAL_AGENT";
/** Role identifier for journalists (US10). */
    public static final String ROLE_JOURNALIST = "JOURNALIST";
    /**
     * Role identifier for ethics committee members (US08).
     */
    public static final String ROLE_ETHICS_COMMITTEE = "ETHICS_COMMITTEE";

    //private final ApplicationSession applicationSession;
    private final AuthenticationRepository authenticationRepository;

    /**
     * Instantiates a new Authentication controller.
     */
    public AuthenticationController() {
        this.authenticationRepository = Repositories.getInstance().getAuthenticationRepository();
    }

    /**
     * Do login boolean.
     *
     * @param email the email
     * @param pwd   the pwd
     * @return the boolean
     */
    public boolean doLogin(String email, String pwd) {
        try {
            return authenticationRepository.doLogin(email, pwd);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Gets user roles.
     *
     * @return the user roles
     */
    public List<UserRoleDTO> getUserRoles() {
        if (authenticationRepository.getCurrentUserSession().isLoggedIn()) {
            return authenticationRepository.getCurrentUserSession().getUserRoles();
        }
        return null;
    }

    /**
     * Do logout.
     */
    public void doLogout() {
        authenticationRepository.doLogout();
    }
}