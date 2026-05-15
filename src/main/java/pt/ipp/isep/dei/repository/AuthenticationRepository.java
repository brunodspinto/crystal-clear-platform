package pt.ipp.isep.dei.repository;

import pt.isep.lei.esoft.auth.AuthFacade;
import pt.isep.lei.esoft.auth.UserSession;

/**
 * The type Authentication repository.
 */
public class AuthenticationRepository {
    private final AuthFacade authenticationFacade;

    /**
     * Instantiates a new Authentication repository.
     */
    public AuthenticationRepository() {
        authenticationFacade = new AuthFacade();
    }

    /**
     * Do login boolean.
     *
     * @param email    the email
     * @param password the password
     * @return the boolean
     */
    public boolean doLogin(String email, String password) {
        return authenticationFacade.doLogin(email, password).isLoggedIn();
    }

    /**
     * Do logout.
     */
    public void doLogout() {
        authenticationFacade.doLogout();
    }

    /**
     * Gets current user session.
     *
     * @return the current user session
     */
    public UserSession getCurrentUserSession() {
        return authenticationFacade.getCurrentUserSession();
    }

    /**
     * Add user role boolean.
     *
     * @param id          the id
     * @param description the description
     * @return the boolean
     */
    public boolean addUserRole(String id, String description) {
        return authenticationFacade.addUserRole(id, description);
    }

    /**
     * Add user with role boolean.
     *
     * @param name     the name
     * @param email    the email
     * @param password the password
     * @param roleId   the role id
     * @return the boolean
     */
    public boolean addUserWithRole(String name, String email, String password, String roleId) {
        return authenticationFacade.addUserWithRole(name, email, password, roleId);
    }
}