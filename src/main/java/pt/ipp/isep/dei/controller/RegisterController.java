package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.domain.UserRole;
import pt.ipp.isep.dei.repository.RegistrationRequestRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Controller for submitting a registration request (US01).
 */
public class RegisterController {

    private final RegistrationRequestRepository registrationRequestRepository;

    /**
     * Instantiates a new Register controller.
     */
    public RegisterController() {
        this.registrationRequestRepository = Repositories.getInstance().getRegistrationRequestRepository();
    }

    /**
     * Instantiates a new Register controller.
     *
     * @param registrationRequestRepository the registration request repository
     */
    public RegisterController(RegistrationRequestRepository registrationRequestRepository) {
        this.registrationRequestRepository = registrationRequestRepository;
    }

    /**
     * Returns the list of roles available for registration.
     *
     * @return the available roles
     */
    public List<UserRole> getAvailableRoles() {
        List<UserRole> roles = new ArrayList<>(Arrays.asList(UserRole.values()));
        roles.remove(UserRole.ADMINISTRATOR);
        return roles;
    }

    /**
     * Returns whether the given role requires an identification document.
     *
     * @param role the role
     * @return the boolean
     */
    public boolean requiresDocument(UserRole role) {
        return role.requiresDocument();
    }

    /**
     * Returns whether the given role needs the extra Political Agent data
     * (national identity card, tax number and mandate start).
     *
     * @param role the role
     * @return the boolean
     */
    public boolean requiresPoliticalData(UserRole role) {
        return role.requiresPoliticalData();
    }

    /**
     * Returns the label describing what document is required for the given role.
     *
     * @param role the role
     * @return the document label
     */
    public String getDocumentLabel(UserRole role) {
        return role.getDocumentLabel();
    }

    /**
     * Validates the password format without creating a full request.
     *
     * @param password the password
     * @return the boolean
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

    /**
     * Submits a Political Agent registration request, collecting the extra data
     * required to create the agent once the request is approved (US01/US02).
     *
     * @param fullName                applicant's full name
     * @param email                   applicant's email
     * @param password                password (must pass validation)
     * @param nationalIdentityCard    national identity card (CC)
     * @param taxIdentificationNumber tax number (NIF)
     * @param mandateStart            mandate start date
     * @return {@code true} if the request was saved, {@code false} if a duplicate exists
     * @throws IllegalArgumentException if any field is invalid or missing
     */
    public boolean submitPoliticalAgentRequest(String fullName, String email, String password,
                                               String nationalIdentityCard, String taxIdentificationNumber,
                                               Date mandateStart) {
        if (nationalIdentityCard == null || nationalIdentityCard.isBlank()) {
            throw new IllegalArgumentException("National identity card is required for a Political Agent.");
        }
        if (taxIdentificationNumber == null || taxIdentificationNumber.isBlank()) {
            throw new IllegalArgumentException("Tax number is required for a Political Agent.");
        }
        if (mandateStart == null) {
            throw new IllegalArgumentException("Mandate start date is required for a Political Agent.");
        }
        RegistrationRequest request = new RegistrationRequest(fullName, email, password,
                UserRole.POLITICAL_AGENT, null, nationalIdentityCard, taxIdentificationNumber, mandateStart);
        return registrationRequestRepository.save(request);
    }
}
