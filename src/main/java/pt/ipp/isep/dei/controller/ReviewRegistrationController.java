package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.domain.UserRole;
import pt.ipp.isep.dei.dto.RegistrationRequestDTO;
import pt.ipp.isep.dei.mapper.RegistrationRequestMapper;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.RegistrationRequestRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.ipp.isep.dei.service.EmailService;
import pt.ipp.isep.dei.service.EmailServiceFactory;
import pt.isep.lei.esoft.auth.UserSession;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;

/**
 * Controller for US02 - Accept/Reject Registration Requests.
 */
public class ReviewRegistrationController {

    private final RegistrationRequestRepository repository;
    private final AuthenticationRepository authRepository;
    private final PoliticalAgentRepository politicalAgentRepository;
    private final EmailService emailService;
    private final RegistrationRequestMapper registrationRequestMapper = new RegistrationRequestMapper();

    /**
     * Instantiates a new Review registration controller.
     */
    public ReviewRegistrationController() {
        this.repository = Repositories.getInstance().getRegistrationRequestRepository();
        this.authRepository = Repositories.getInstance().getAuthenticationRepository();
        this.politicalAgentRepository = Repositories.getInstance().getPoliticalAgentRepository();
        this.emailService = EmailServiceFactory.create();
    }

    /**
     * Instantiates a new Review registration controller.
     *
     * @param repository     the repository
     * @param authRepository the auth repository
     * @param emailService   the email service
     */
    public ReviewRegistrationController(RegistrationRequestRepository repository,
                                        AuthenticationRepository authRepository,
                                        EmailService emailService) {
        this(repository, authRepository, new PoliticalAgentRepository(), emailService);
    }

    /**
     * Instantiates a new Review registration controller.
     *
     * @param repository               the repository
     * @param authRepository           the auth repository
     * @param politicalAgentRepository the political agent repository
     * @param emailService             the email service
     */
    public ReviewRegistrationController(RegistrationRequestRepository repository,
                                        AuthenticationRepository authRepository,
                                        PoliticalAgentRepository politicalAgentRepository,
                                        EmailService emailService) {
        this.repository = repository;
        this.authRepository = authRepository;
        this.politicalAgentRepository = politicalAgentRepository;
        this.emailService = emailService;
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
     * Sends an approval notification to the user (AC1).
     *
     * @param request the request
     */
    public void approveRequest(RegistrationRequest request) {
        request.approve();
        String roleId = roleIdFor(request);
        authRepository.addUserWithRole(request.getFullName(), request.getEmail(),
                request.getPassword(), roleId);
        registerPoliticalAgentIfNeeded(request);
        emailService.sendNotification(
                request.getEmail(),
                "Registration Approved",
                "Dear " + request.getFullName() + ",\n\nYour registration request has been approved. You may now log in."
        );
    }

    /**
     * Creates the {@link PoliticalAgent} domain object when an approved request
     * is for the Political Agent role and carries the required data (national
     * identity card, tax number and mandate start). Without this step the agent
     * could log in but would not show up in the agent lists nor be able to
     * submit a declaration. Requests that do not carry this data are left
     * untouched, so the other roles keep working exactly as before.
     *
     * @param request the approved registration request
     */
    private void registerPoliticalAgentIfNeeded(RegistrationRequest request) {
        if (request.getRole() != UserRole.POLITICAL_AGENT) {
            return;
        }
        if (request.getTaxIdentificationNumber() == null
                || request.getNationalIdentityCard() == null
                || request.getMandateStart() == null) {
            return;
        }
        PoliticalAgent agent = new PoliticalAgent(request.getFullName(), request.getEmail(),
                request.getNationalIdentityCard(), request.getTaxIdentificationNumber(),
                request.getMandateStart(), null);
        politicalAgentRepository.save(agent);
    }

    /**
     * Rejects a registration request with a given reason.
     * Sends a rejection notification to the user (AC1).
     *
     * @param request the request to reject.
     * @param reason  the reason for rejection.
     */
    public void rejectRequest(RegistrationRequest request, String reason) {
        request.reject(reason);
        emailService.sendNotification(
                request.getEmail(),
                "Registration Rejected",
                "Dear " + request.getFullName() + ",\n\nYour registration request has been rejected.\nReason: " + reason
        );
    }

    /**
     * Returns the pending requests as DTOs. Caller must be an administrator.
     *
     * @return list of pending request DTOs
     */
    public List<RegistrationRequestDTO> getPendingRequestsAsDTO() {
        requireAdminSession();
        List<RegistrationRequest> pending = repository.getPendingRequests();
        return registrationRequestMapper.toDTO(pending);
    }

    /**
     * Approves the pending request identified by email. Caller must be an administrator.
     *
     * @param email the email of the request to approve
     */
    public void approveRequestByEmail(String email) {
        requireAdminSession();
        RegistrationRequest request = repository.findPendingByEmail(email);
        if (request == null) {
            throw new IllegalArgumentException("No pending request found for: " + email);
        }
        approveRequest(request);
    }

    /**
     * Rejects the pending request identified by email. Caller must be an administrator.
     *
     * @param email  the email of the request to reject
     * @param reason the reason for rejection
     */
    public void rejectRequestByEmail(String email, String reason) {
        requireAdminSession();
        RegistrationRequest request = repository.findPendingByEmail(email);
        if (request == null) {
            throw new IllegalArgumentException("No pending request found for: " + email);
        }
        rejectRequest(request, reason);
    }

    private void requireAdminSession() {
        UserSession session = authRepository.getCurrentUserSession();
        if (session == null || !session.isLoggedIn()) {
            throw new IllegalStateException("No active session.");
        }
        if (!sessionHasAdminRole(session)) {
            throw new IllegalStateException("Only administrators may perform this action.");
        }
    }

    private boolean sessionHasAdminRole(UserSession session) {
        List<UserRoleDTO> roles = session.getUserRoles();
        if (roles == null) {
            return false;
        }
        boolean found = false;
        int i = 0;
        while (!found && i < roles.size()) {
            if (AuthenticationController.ROLE_ADMIN.equals(roles.get(i).getDescription())) {
                found = true;
            }
            i++;
        }
        return found;
    }

    private static String roleIdFor(RegistrationRequest request) {
        return request.getRole().name();
    }
}
