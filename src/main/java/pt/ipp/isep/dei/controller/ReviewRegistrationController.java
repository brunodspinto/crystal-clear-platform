package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.domain.UserRole;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.RegistrationRequestRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.List;

/**
 * Controller for US02 - Accept/Reject Registration Requests.
 */
public class ReviewRegistrationController {

    private final RegistrationRequestRepository repository;
    private final AuthenticationRepository authRepository;

    /**
     * Instantiates a new Review registration controller.
     */
    public ReviewRegistrationController() {
        this.repository = Repositories.getInstance().getRegistrationRequestRepository();
        this.authRepository = Repositories.getInstance().getAuthenticationRepository();
    }

    /**
     * Instantiates a new Review registration controller.
     *
     * @param repository     the repository
     * @param authRepository the auth repository
     */
    public ReviewRegistrationController(RegistrationRequestRepository repository,
                                        AuthenticationRepository authRepository) {
        this.repository = repository;
        this.authRepository = authRepository;
    }

    /**
     * Returns the list of registration requests that are still waiting for a decision.
     *
     * @return list of pending registration requests.
     */
    public List<RegistrationRequest> getPendingRequests() {
        return repository.getPendingRequests();
    }

    /**
     * Approves the request and creates an authenticated user account with the requested role (AC3).
     *
     * @param request the request
     */
    public void approveRequest(RegistrationRequest request) {
        request.approve();
        String roleId = roleIdFor(request.getRole());
        authRepository.addUserWithRole(request.getFullName(), request.getEmail(),
                request.getPassword(), roleId);
    }

    /**
     * Rejects a registration request with a given reason.
     *
     * @param request the request to reject.
     * @param reason  the reason for rejection.
     */
    public void rejectRequest(RegistrationRequest request, String reason) {
        request.reject(reason);
    }

    private static String roleIdFor(UserRole role) {
        return role.name();
    }
}
