package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;

public class AuthenticationController {

    /** Role identifier for administrators. */
    public static final String ROLE_ADMIN = "ADMINISTRATOR";
    /** Role identifier for citizens (US12). */
    public static final String ROLE_CITIZEN = "CITIZEN";
    /** Role identifier for political agents (US06). */
    public static final String ROLE_POLITICAL_AGENT = "POLITICAL_AGENT";
    /** Role identifier for product owners (US24). */
    public static final String ROLE_PRODUCT_OWNER = "PRODUCT_OWNER";
    /** Role identifier for journalists (US10). */
    public static final String ROLE_JOURNALIST = "JOURNALIST";
    /** Role identifier for ethics committee members (US08). */
    public static final String ROLE_ETHICS_COMMITTEE = "ETHICS_COMMITTEE";

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