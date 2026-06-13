package pt.ipp.isep.dei.dto;

import pt.ipp.isep.dei.domain.UserRole;

import java.util.Date;

/**
 * Data transfer object for a pending registration request.
 * Used to pass display data to the UI without exposing the domain entity.
 */
public class RegistrationRequestDTO {

    private final String fullName;
    private final String email;
    private final UserRole role;
    private final Date submissionDate;
    private final String identificationDocument;
    private final String nationalIdentityCard;
    private final String taxIdentificationNumber;
    private final Date mandateStart;

    /**
     * Instantiates a new Registration request dto.
     *
     * @param fullName                the full name
     * @param email                   the email
     * @param role                    the role
     * @param submissionDate          the submission date
     * @param identificationDocument  the identification document
     * @param nationalIdentityCard    the Political Agent national identity card, or null
     * @param taxIdentificationNumber the Political Agent tax number, or null
     * @param mandateStart            the Political Agent mandate start date, or null
     */
    public RegistrationRequestDTO(String fullName, String email, UserRole role,
                                   Date submissionDate, String identificationDocument,
                                   String nationalIdentityCard, String taxIdentificationNumber,
                                   Date mandateStart) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.submissionDate = submissionDate;
        this.identificationDocument = identificationDocument;
        this.nationalIdentityCard = nationalIdentityCard;
        this.taxIdentificationNumber = taxIdentificationNumber;
        this.mandateStart = mandateStart;
    }

    /**
     * Gets full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets role.
     *
     * @return the role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Gets submission date.
     *
     * @return the submission date
     */
    public Date getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Gets identification document.
     *
     * @return the identification document
     */
    public String getIdentificationDocument() {
        return identificationDocument;
    }

    /**
     * Gets the Political Agent national identity card, or null for other roles.
     *
     * @return the national identity card
     */
    public String getNationalIdentityCard() {
        return nationalIdentityCard;
    }

    /**
     * Gets the Political Agent tax number, or null for other roles.
     *
     * @return the tax identification number
     */
    public String getTaxIdentificationNumber() {
        return taxIdentificationNumber;
    }

    /**
     * Gets the Political Agent mandate start date, or null for other roles.
     *
     * @return the mandate start date
     */
    public Date getMandateStart() {
        return mandateStart;
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
