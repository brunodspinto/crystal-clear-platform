package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.RegistrationRequestRepository;
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
    /**
     * Role identifier for journalists (US10).
     */
    public static final String ROLE_JOURNALIST = "JOURNALIST";
    /**
     * Role identifier for ethics committee members (US08).
     */
    public static final String ROLE_ETHICS_COMMITTEE = "ETHICS_COMMITTEE";

    //private final ApplicationSession applicationSession;
    private final AuthenticationRepository authenticationRepository;
    private final RegistrationRequestRepository registrationRequestRepository;

    /**
     * Instantiates a new Authentication controller.
     */
    public AuthenticationController() {
        Repositories repositories = Repositories.getInstance();
        this.authenticationRepository = repositories.getAuthenticationRepository();
        this.registrationRequestRepository = repositories.getRegistrationRequestRepository();
    }

    /**
     * Do login boolean.
     *
     * @param email    the email
     * @param password the password
     * @return the boolean
     */
    public boolean doLogin(String email, String password) {
        try {
            return authenticationRepository.doLogin(email, password);
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

    /**
     * Returns the rejection reason if the given email belongs to a registration
     * request that was rejected by the Administrator. Used by the login screen
     * to tell a rejected applicant why they cannot log in.
     *
     * @param email the email used in the login attempt
     * @return the rejection reason, or {@code null} if there is no rejected
     *         request for that email
     */
    public String getRejectionReason(String email) {
        RegistrationRequest rejected = registrationRequestRepository.findRejectedByEmail(email);
        if (rejected == null) {
            return null;
        }
        return rejected.getRejectionReason();
    }

    /**
     * If the given email belongs to an approved registration request that has
     * not yet seen its welcome notification, marks it as shown and returns the
     * applicant's full name. Used by the login screen to greet a newly approved
     * user only on their first successful login.
     *
     * @param email the email used in the successful login
     * @return the applicant's full name on the first login after approval,
     *         or {@code null} otherwise
     */
    public String consumeFirstLoginWelcome(String email) {
        RegistrationRequest approved = registrationRequestRepository.findApprovedByEmail(email);
        if (approved == null || approved.isWelcomeShown()) {
            return null;
        }
        approved.markWelcomeShown();
        return approved.getFullName();
    }
}