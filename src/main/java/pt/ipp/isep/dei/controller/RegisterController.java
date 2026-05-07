package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.domain.UserRole;
import pt.ipp.isep.dei.repository.RegistrationRequestRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.Arrays;
import java.util.List;

/**
 * Controller for submitting a registration request (US01).
 */
public class RegisterController {

    private final RegistrationRequestRepository registrationRequestRepository;

    public RegisterController() {
        this.registrationRequestRepository = Repositories.getInstance().getRegistrationRequestRepository();
    }

    public RegisterController(RegistrationRequestRepository registrationRequestRepository) {
        this.registrationRequestRepository = registrationRequestRepository;
    }

    /**
     * Returns the list of roles available for registration.
     */
    public List<UserRole> getAvailableRoles() {
        return Arrays.asList(UserRole.values());
    }

    /**
     * Returns whether the given role requires an identification document.
     */
    public boolean requiresDocument(UserRole role) {
        return role == UserRole.JOURNALIST || role == UserRole.CITIZEN;
    }

    /**
     * Returns the label describing what document is required for the given role.
     */
    public String getDocumentLabel(UserRole role) {
        if (role == UserRole.JOURNALIST) {
            return "Press card number (Cartão de Jornalista): ";
        }
        if (role == UserRole.CITIZEN) {
            return "National identity card number (Cartão de Cidadão): ";
        }
        return null;
    }

    /**
     * Validates the password format without creating a full request.
     */
    public boolean isValidPassword(String password) {
        return RegistrationRequest.isValidPassword(password);
    }

    /**
     * Submits a registration request.
     *
     * @param fullName               applicant's full name
     * @param email                  applicant's email
     * @param password               password (must pass validation)
     * @param role                   selected role
     * @param identificationDocument identification document (may be null for roles that don't require it)
     * @return {@code true} if the request was saved, {@code false} if a duplicate exists
     * @throws IllegalArgumentException if any field is invalid
     */
    public boolean submitRequest(String fullName, String email, String password,
                                  UserRole role, String identificationDocument) {
        RegistrationRequest request = new RegistrationRequest(fullName, email, password, role, identificationDocument);
        return registrationRequestRepository.save(request);
    }
}
