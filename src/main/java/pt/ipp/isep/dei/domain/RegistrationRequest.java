package pt.ipp.isep.dei.domain;

import java.util.Date;

/**
 * Represents a registration request submitted by a future user (US01).
 * Remains PENDING until an Administrator approves or rejects it (US02).
 */
public class RegistrationRequest {

    private final String fullName;
    private final String email;
    private final String password;
    private final UserRole role;
    private final String identificationDocument;
    private final Date submissionDate;
    private RegistrationStatus status;
    private String rejectionReason;

    /**
     * Creates a registration request. Validates the password format and required fields.
     *
     * @param fullName               applicant's full name
     * @param email                  applicant's email address
     * @param password               password (7 alphanumeric chars, ≥3 uppercase, ≥2 digits)
     * @param role                   selected role
     * @param identificationDocument press card (Journalist) or national ID (Citizen); null otherwise
     * @throws IllegalArgumentException if any required field is blank or the password is invalid
     */
    public RegistrationRequest(String fullName, String email, String password,
                                UserRole role, String identificationDocument) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role is required.");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException(
                    "Password must have exactly 7 alphanumeric characters, at least 3 uppercase letters and at least 2 digits.");
        }
        if ((role == UserRole.JOURNALIST || role == UserRole.CITIZEN)
                && (identificationDocument == null || identificationDocument.isBlank())) {
            throw new IllegalArgumentException("Identification document is required for this role.");
        }

        this.fullName = fullName.trim();
        this.email = email.trim();
        this.password = password;
        this.role = role;
        this.identificationDocument = (identificationDocument != null) ? identificationDocument.trim() : null;
        this.submissionDate = new Date();
        this.status = RegistrationStatus.PENDING;
    }

    /**
     * Validates the password format: exactly 7 alphanumeric characters,
     * at least 3 uppercase letters, and at least 2 digits.
     *
     * @param password the password
     * @return the boolean
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() != 7) {
            return false;
        }
        int upperCount = 0;
        int digitCount = 0;
        for (char c : password.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
            if (Character.isUpperCase(c)) {
                upperCount++;
            }
            if (Character.isDigit(c)) {
                digitCount++;
            }
        }
        return upperCount >= 3 && digitCount >= 2;
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
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
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
     * Gets identification document.
     *
     * @return the identification document
     */
    public String getIdentificationDocument() {
        return identificationDocument;
    }

    /**
     * Gets submission date.
     *
     * @return the submission date
     */
    public Date getSubmissionDate() {
        return new Date(submissionDate.getTime());
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public RegistrationStatus getStatus() {
        return status;
    }

    /**
     * Gets rejection reason.
     *
     * @return the rejection reason
     */
    public String getRejectionReason() {
        return rejectionReason;
    }

    /**
     * Marks the request as approved.
     *
     * @throws IllegalStateException if the request is not PENDING
     */
    public void approve() {
        if (status != RegistrationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be approved.");
        }
        status = RegistrationStatus.APPROVED;
    }

    /**
     * Marks the request as rejected with a reason.
     *
     * @param reason the reason provided by the Administrator
     * @throws IllegalStateException    if the request is not PENDING
     * @throws IllegalArgumentException if the reason is blank
     */
    public void reject(String reason) {
        if (status != RegistrationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be rejected.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason is required.");
        }
        status = RegistrationStatus.REJECTED;
        rejectionReason = reason.trim();
    }

    /**
     * Two requests are the same if they share the same email and role.
     * This supports the AC5 duplicate check.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationRequest other = (RegistrationRequest) o;
        return email.equalsIgnoreCase(other.email) && role == other.role;
    }

    @Override
    public String toString() {
        return String.format("RegistrationRequest{name='%s', email='%s', role=%s, status=%s}",
                fullName, email, role, status);
    }
}
